package com.stox.core.downloader;

import java.util.List;

public interface Downloader<T, V> {

	List<T> download() throws Exception;

	T parse(final V tokens) throws Exception;

}
