package com.stox.core.repository;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.util.Constant;

@Component
public class BinaryFileBarRepository implements BarRepository {

	@Override
	public Date getLastTradingDate(String instrumentId, BarSpan barSpan) {
		final String path = getPath(instrumentId, barSpan).intern();
		synchronized (path) {
			try (final RandomAccessFile file = new RandomAccessFile(path, "r")) {
				if (file.length() >= Bar.BYTES) {
					file.seek(file.length() - Bar.BYTES);
					return new Date(file.readLong());
				}
			} catch (Exception ignored) {
			}
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -2);
			return calendar.getTime();
		}
	}

	@Override
	public List<Bar> find(String instrumentId, BarSpan barSpan, Date from, Date to) {
		final String path = getPath(instrumentId, barSpan).intern();
		synchronized (path) {
			try (final RandomAccessFile file = new RandomAccessFile(path, "r")) {
				final List<Bar> bars = new ArrayList<>();
				file.seek(file.length());
				while (file.getFilePointer() >= Bar.BYTES) {
					file.seek(file.getFilePointer() - Bar.BYTES);
					final Bar bar = read(file);
					if (bar.getDate().before(from)) {
						break;
					}
					if (bar.getDate().before(to)) {
						bars.add(bar);
					}
					if (file.getFilePointer() >= Bar.BYTES * 2) {
						file.seek(file.getFilePointer() - Bar.BYTES);
					} else {
						break;
					}
				}
				return bars;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void save(List<Bar> bars, String instrumentId, BarSpan barSpan) {
		final TreeSet<Bar> set = new TreeSet<Bar>(Collections.reverseOrder());
		set.addAll(bars);
		final String path = getPath(instrumentId, barSpan).intern();
		synchronized (path) {
			new File(path).getParentFile().mkdirs();
			try (final RandomAccessFile file = new RandomAccessFile(path, "rw")) {
				final long date = set.last().getDate().getTime();
				if (file.length() >= Bar.BYTES) {
					file.seek(file.length() - Bar.BYTES);
					Bar original = read(file);
					while (date <= original.getDate().getTime()) {
						set.add(original);
						file.seek(Math.max(0, file.getFilePointer() - Bar.BYTES * 2));
						if (file.getFilePointer() < Bar.BYTES) {
							break;
						} else {
							original = read(file);
						}
					}
				}
				set.forEach(bar -> write(bar, file));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void save(Bar bar, String instrumentId, BarSpan barSpan) {
		final String path = getPath(instrumentId, barSpan);
		synchronized (path) {
			new File(path).getParentFile().mkdirs();
			try (final RandomAccessFile file = new RandomAccessFile(path, "rw")) {
				file.seek(file.length());
				write(bar, file);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected String getPath(final String instrumentId, final BarSpan barSpan) {
		return Constant.PATH + "bar" + File.separator + barSpan.getShortName() + File.separator + instrumentId;
	}

	public Bar read(final RandomAccessFile file) {
		try {
			final Bar bar = new Bar();
			bar.setDate(new Date(file.readLong()));
			bar.setOpen(file.readDouble());
			bar.setHigh(file.readDouble());
			bar.setLow(file.readDouble());
			bar.setClose(file.readDouble());
			bar.setPreviousClose(file.readDouble());
			bar.setVolume(file.readDouble());
			return bar;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void write(final Bar bar, final RandomAccessFile file) {
		try {
			file.writeLong(bar.getDate().getTime());
			file.writeDouble(bar.getOpen());
			file.writeDouble(bar.getHigh());
			file.writeDouble(bar.getLow());
			file.writeDouble(bar.getClose());
			file.writeDouble(bar.getPreviousClose());
			file.writeDouble(bar.getVolume());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}