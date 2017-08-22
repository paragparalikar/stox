package com.stox.watchlist.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
		final ValueWrapper wrapper = cache.get(ALL);
		return null == wrapper ? Collections.emptyList() : (List<Watchlist>) wrapper.get();
	}

	@Override
	@CachePut(value = CACHE, key = "#a0.getId()")
	public Watchlist save(Watchlist watchlist) throws Exception {
		load();
		if (null == watchlist.getId() || 0 == watchlist.getId()) {
			watchlist.setId(idGenerator.incrementAndGet());
		}
		write(watchlist);
		FileUtil.getFile(WatchlistRepositoryUtil.getWatchlistFilePath(watchlist.getId()));
		return watchlist;
	}

	@Override
	@CacheEvict(value = CACHE, key="#a0")
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
	
	private void write(final Watchlist watchlist) throws Exception {
		final File file = FileUtil.getFile(getPath());
		try(final FileOutputStream fos = new FileOutputStream(file, true)){
			fos.write((watchlist.getId()+","+watchlist.getName()+File.separator).getBytes());
		}
	}
	
	private void writeAll(final List<Watchlist> watchlists) throws Exception {
		final File file = FileUtil.getFile(getPath());
		try(final FileOutputStream fos = new FileOutputStream(file, false)){
			for(final Watchlist watchlist : watchlists) {
				fos.write((watchlist.getId()+","+watchlist.getName()+File.separator).getBytes());
			}
		}
	}

	private Watchlist parse(final String text) {
		final String tokens[] = text.split(",");
		final Watchlist watchlist = new Watchlist();
		watchlist.setId(Integer.parseInt(tokens[0]));
		watchlist.setName(tokens[1]);
		return watchlist;
	}

	private List<Watchlist> readAll() throws Exception{
		final File file = FileUtil.getFile(getPath());
		return Files.readAllLines(file.toPath()).stream().map(text -> parse(text)).collect(Collectors.toList());
	}
}
