package com.stox.watchlist.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.util.Constant;
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

	private final CsvSchema schema = Constant.csvMapper.schemaFor(Watchlist.class).withHeader();

	private String getPath() {
		return WatchlistConstant.PATH + "csv";
	}

	private synchronized void load() throws IOException {
		if (null == cache) {
			cache = cacheManager.getCache(CACHE);
			final File file = FileUtil.getFile(getPath());
			if (0 < file.length()) {
				final ObjectReader reader = Constant.csvMapper.reader(schema).forType(Watchlist.class);
				final List<Watchlist> watchlists = reader.<Watchlist>readValues(file).readAll();
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
		final File file = FileUtil.getFile(getPath());
		Constant.csvMapper.writerFor(Watchlist.class).writeValue(new FileOutputStream(file, true), watchlist);
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
		Constant.csvMapper.writer(schema).writeValues(new FileOutputStream(getPath(), false)).writeAll(watchlists)
				.flush();
		return watchlist;
	}

}
