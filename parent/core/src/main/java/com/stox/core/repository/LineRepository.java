package com.stox.core.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

import com.stox.core.util.Constant;

public class LineRepository {

	private final String path;

	public LineRepository(final String path) {
		this.path = Constant.PATH + path;
	}

	public synchronized List<String> findAll() {
		try {
			return Files.readAllLines(Paths.get(path));
		} catch (IOException e) {
			return Collections.emptyList();
		}
	}

	public synchronized void append(final String instrumentId) {
		try {
			Files.write(Paths.get(path), (instrumentId + Constant.LINEFEED).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void drop() {
		try {
			Files.deleteIfExists(Paths.get(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
