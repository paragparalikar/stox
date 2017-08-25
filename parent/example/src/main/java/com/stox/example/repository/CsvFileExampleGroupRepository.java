package com.stox.example.repository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
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
import com.stox.example.model.ExampleGroup;
import com.stox.example.model.ExampleGroupExistsException;
import com.stox.example.util.ExampleConstant;

@Lazy
@Component
public class CsvFileExampleGroupRepository implements ExampleGroupRepository {
	private static final String ALL = "all";
	private static final String CACHE = "exampleGroups";

	@Autowired
	private CacheManager cacheManager;

	private volatile Cache cache;

	private final AtomicInteger idGenerator = new AtomicInteger(0);

	private String getPath() {
		return ExampleConstant.PATH + "csv";
	}

	private synchronized void load() throws Exception {
		if (null == cache) {
			cache = cacheManager.getCache(CACHE);
			final File file = FileUtil.getFile(getPath());
			if (0 < file.length()) {
				final List<ExampleGroup> exampleGroups = readAll();
				cache.put(ALL, exampleGroups);
				exampleGroups.forEach(exampleGroup -> {
					cache.put(exampleGroup.getId(), exampleGroup);
					idGenerator.set(Math.max(idGenerator.get(), exampleGroup.getId()));
				});
			}
		}
	}

	@Override
	@Cacheable(value = CACHE, key = "'" + ALL + "'")
	@SuppressWarnings("unchecked")
	public List<ExampleGroup> loadAll() throws Exception {
		load();
		final ValueWrapper wrapper = cache.get(ALL);
		return null == wrapper ? Collections.emptyList() : (List<ExampleGroup>) wrapper.get();
	}

	@Override
	@CachePut(value = CACHE, key = "#a0.getId()")
	public ExampleGroup save(ExampleGroup exampleGroup) throws Exception {
		if (null == exampleGroup.getId() || 0 == exampleGroup.getId()) {
			create(exampleGroup);
		}else {
			update(exampleGroup);
		}
		return exampleGroup;
	}

	private void create(final ExampleGroup exampleGroup) throws Exception {
		final List<ExampleGroup> exampleGroups = loadAll();
		final Predicate<ExampleGroup> duplicateNamePredicate = w -> w.getName().equalsIgnoreCase(exampleGroup.getName());
		if (exampleGroups.stream().filter(duplicateNamePredicate).findFirst().isPresent()) {
			throw new ExampleGroupExistsException(exampleGroup);
		} else {
			exampleGroup.setId(idGenerator.incrementAndGet());
			append(exampleGroup);
			exampleGroups.add(exampleGroup);
			FileUtil.getFile(ExampleRepositoryUtil.getExampleGroupFilePath(exampleGroup.getId()));
		}
	}

	private void update(final ExampleGroup exampleGroup) throws Exception {
		final List<ExampleGroup> exampleGroups = loadAll();
		final Predicate<ExampleGroup> duplicateNamePredicate = w -> w.getName().equalsIgnoreCase(exampleGroup.getName())
				&& !w.getId().equals(exampleGroup.getId());
		if (exampleGroups.stream().filter(duplicateNamePredicate).findFirst().isPresent()) {
			throw new ExampleGroupExistsException(exampleGroup);
		} else {
			exampleGroups.stream().filter(w -> w.getId().equals(exampleGroup.getId())).findFirst().ifPresent(w -> w.copy(exampleGroup));
			writeAll(exampleGroups);
		}
	}

	@Override
	@CacheEvict(value = CACHE, key = "#a0")
	public ExampleGroup delete(Integer exampleGroupId) throws Exception {
		load();
		Files.deleteIfExists(Paths.get(ExampleRepositoryUtil.getExampleGroupFilePath(exampleGroupId)));
		final ValueWrapper wrapper = cache.get(exampleGroupId);
		final ExampleGroup exampleGroup = null == wrapper ? null : (ExampleGroup) wrapper.get();
		final List<ExampleGroup> exampleGroups = loadAll();
		exampleGroups.remove(exampleGroup);
		writeAll(exampleGroups);
		return exampleGroup;
	}

	private String toString(final ExampleGroup exampleGroup) {
		return exampleGroup.getId() + "," + exampleGroup.getName();
	}

	private void append(final ExampleGroup exampleGroup) throws Exception {
		final File file = FileUtil.getFile(getPath());
		Files.write(file.toPath(), Arrays.asList(new String[] { toString(exampleGroup) }), StandardOpenOption.APPEND);
	}

	private void writeAll(final List<ExampleGroup> exampleGroups) throws Exception {
		final File file = FileUtil.getFile(getPath());
		final List<String> lines = exampleGroups.stream().map(exampleGroup -> toString(exampleGroup))
				.collect(Collectors.toList());
		Files.write(file.toPath(), lines, StandardOpenOption.TRUNCATE_EXISTING);
	}

	private ExampleGroup parse(final String text) {
		final String tokens[] = text.split(",");
		final ExampleGroup exampleGroup = new ExampleGroup();
		exampleGroup.setId(Integer.parseInt(tokens[0]));
		exampleGroup.setName(tokens[1]);
		return exampleGroup;
	}

	private List<ExampleGroup> readAll() throws Exception {
		final File file = FileUtil.getFile(getPath());
		return Files.readAllLines(file.toPath()).stream().map(text -> parse(text)).collect(Collectors.toList());
	}
}
