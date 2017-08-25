package com.stox.watchlist.repository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.stox.core.util.FileUtil;
import com.stox.watchlist.model.Watchlist;
import com.stox.watchlist.model.WatchlistExistsException;
import com.stox.watchlist.util.WatchlistConstant;

@Lazy
@Component
public class CsvFileWatchlistRepository implements WatchlistRepository {
	private static final String ALL = "all";
	private static final String CACHE = "watchlists";

	@Autowired
	private CacheManager cacheManager;

	private volatile Cache cache;

	private final AtomicInteger idGenerator = new AtomicInteger(0);

	private String getPath() {
		return WatchlistConstant.PATH + "csv";
	}

	private synchronized void load() throws Exception {
		if (null == cache) {
			cache = cacheManager.getCache(CACHE);
			final File file = FileUtil.getFile(getPath());
			if (0 < file.length()) {
				final List<Watchlist> watchlists = readAll();
				cache.put(ALL, watchlists);
				watchlists.forEach(watchlist -> {
					cache.put(watchlist.getId(), watchlist);
					idGenerator.set(Math.max(idGenerator.get(), watchlist.getId()));
				});
			}
		}
	}

	@Override
	@Cacheable(value = CACHE, key = "'" + ALL + "'")
	@SuppressWarnings("unchecked")
	public List<Watchlist> loadAll() throws Exception {
		load();
		ValueWrapper wrapper = cache.get(ALL);
		cache.put(ALL, new LinkedList<>());
		wrapper = cache.get(ALL);
		return null == wrapper ? Collections.emptyList() : (List<Watchlist>) wrapper.get();
	}

	@Override
	@CachePut(value = CACHE, key = "#a0.getId()")
	public Watchlist save(Watchlist watchlist) throws Exception {
		if (null == watchlist.getId() || 0 == watchlist.getId()) {
			create(watchlist);
		}else {
			update(watchlist);
		}
		return watchlist;
	}

	private void create(final Watchlist watchlist) throws Exception {
		final List<Watchlist> watchlists = loadAll();
		final Predicate<Watchlist> duplicateNamePredicate = w -> w.getName().equalsIgnoreCase(watchlist.getName());
		if (watchlists.stream().filter(duplicateNamePredicate).findFirst().isPresent()) {
			throw new WatchlistExistsException(watchlist);
		} else {
			watchlist.setId(idGenerator.incrementAndGet());
			append(watchlist);
			watchlists.add(watchlist);
			FileUtil.getFile(WatchlistRepositoryUtil.getWatchlistFilePath(watchlist.getId()));
		}
	}

	private void update(final Watchlist watchlist) throws Exception {
		final List<Watchlist> watchlists = loadAll();
		final Predicate<Watchlist> duplicateNamePredicate = w -> w.getName().equalsIgnoreCase(watchlist.getName())
				&& !w.getId().equals(watchlist.getId());
		if (watchlists.stream().filter(duplicateNamePredicate).findFirst().isPresent()) {
			throw new WatchlistExistsException(watchlist);
		} else {
			watchlists.stream().filter(w -> w.getId().equals(watchlist.getId())).findFirst().ifPresent(w -> w.copy(watchlist));
			writeAll(watchlists);
		}
	}

	@Override
	@CacheEvict(value = CACHE, key = "#a0")
	public Watchlist delete(Integer watchlistId) throws Exception {
		load();
		Files.deleteIfExists(Paths.get(WatchlistRepositoryUtil.getWatchlistFilePath(watchlistId)));
		final ValueWrapper wrapper = cache.get(watchlistId);
		final Watchlist watchlist = null == wrapper ? null : (Watchlist) wrapper.get();
		final List<Watchlist> watchlists = loadAll();
		watchlists.remove(watchlist);
		writeAll(watchlists);
		return watchlist;
	}

	private String toString(final Watchlist watchlist) {
		return watchlist.getId() + "," + watchlist.getName();
	}

	private void append(final Watchlist watchlist) throws Exception {
		final File file = FileUtil.getFile(getPath());
		Files.write(file.toPath(), Arrays.asList(new String[] { toString(watchlist) }), StandardOpenOption.APPEND);
	}

	private void writeAll(final List<Watchlist> watchlists) throws Exception {
		final File file = FileUtil.getFile(getPath());
		final List<String> lines = watchlists.stream().map(watchlist -> toString(watchlist))
				.collect(Collectors.toList());
		Files.write(file.toPath(), lines, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private Watchlist parse(final String text) {
		final String tokens[] = text.split(",");
		final Watchlist watchlist = new Watchlist();
		watchlist.setId(Integer.parseInt(tokens[0]));
		watchlist.setName(tokens[1]);
		return watchlist;
	}

	private List<Watchlist> readAll() throws Exception {
		final File file = FileUtil.getFile(getPath());
		return Files.readAllLines(file.toPath()).stream().map(text -> parse(text)).collect(Collectors.toList());
	}
}
