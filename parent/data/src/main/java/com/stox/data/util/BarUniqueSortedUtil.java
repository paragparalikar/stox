package com.stox.data.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TreeSet;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.BinaryFileBarRepository;
import com.stox.core.repository.CsvFileInstrumentRepository;

public class BarUniqueSortedUtil {

	public static void main(String[] args) {
		final BarRepository barRepository = new BinaryFileBarRepository();
		final CacheManager cacheManager = new ConcurrentMapCacheManager();
		final CsvFileInstrumentRepository instrumentRespository = new CsvFileInstrumentRepository();
		instrumentRespository.setCacheManager(cacheManager);
		instrumentRespository.getAllInstruments().parallelStream().forEach(instrument -> {
			try {
				final TreeSet<Bar> set = new TreeSet<Bar>(Collections.reverseOrder());
				set.addAll(barRepository.find(instrument.getId(), BarSpan.D, new Date(0), new Date()));
				barRepository.drop(instrument, BarSpan.D);
				barRepository.save(new ArrayList<Bar>(set), instrument.getId(), BarSpan.D);
				System.out.println(set.size() + "\t" + instrument);
			} catch (final Exception exception) {
				exception.printStackTrace();
			}
		});
	}

}
