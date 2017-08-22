package com.stox.watchlist.repository;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.Constant;
import com.stox.core.util.FileUtil;
import com.stox.core.util.StringUtil;
import com.stox.watchlist.model.WatchlistEntry;
import com.stox.watchlist.model.WatchlistEntryExistsException;

@Lazy
@Component
public class CsvFileWatchlistEntryRepository implements WatchlistEntryRepository {
	private static final String ALL = "all";
	private static final String CACHE = "watchlists";
	
	@Autowired
	private InstrumentRepository instrumentRepository;

	private final CsvSchema schema = Constant.csvMapper.schemaFor(WatchlistEntry.class).withHeader();

	@Override
	public List<WatchlistEntry> load(Integer watchlistId) throws Exception {
		final File file = FileUtil.getFile(WatchlistRepositoryUtil.getWatchlistFilePath(watchlistId));
		final List<WatchlistEntry> entries = Constant.csvMapper.reader(schema).forType(WatchlistEntry.class).<WatchlistEntry>readValues(file)
				.readAll();
		entries.forEach(entry -> entry.setInstrument(instrumentRepository.getInstrument(entry.getInstrumentId())));
		return entries;
	}

	@Override
	public WatchlistEntry save(WatchlistEntry entry) throws Exception {
		final List<WatchlistEntry> entries = load(entry.getWatchlistId());
		if (!StringUtil.hasText(entry.getId())) {
			final Predicate<WatchlistEntry> predicate = e -> e.getInstrumentId()
					.equals(entry.getInstrumentId()) && e.getBarSpan().equals(entry.getBarSpan());
			entries.stream().filter(predicate).findFirst().ifPresent(e -> {
				throw new WatchlistEntryExistsException(entry);
			});
			entry.setId(UUID.randomUUID().toString());
		} else {
			entries.removeIf(watchlistEntry -> watchlistEntry.getId().equals(entry.getId()));
		}
		entries.add(entry);
		final File file = FileUtil.getFile(WatchlistRepositoryUtil.getWatchlistFilePath(entry.getWatchlistId()));
		Constant.csvMapper.writer(schema).writeValues(file).writeAll(entries).flush();
		return entry;
	}

	@Override
	public WatchlistEntry delete(final Integer watchlistId, final String entryId) throws Exception {
		final List<WatchlistEntry> entries = load(watchlistId);
		final Optional<WatchlistEntry> optionalEntry = entries.stream().filter(entry -> entry.getId().equals(entryId))
				.findFirst();
		optionalEntry.ifPresent(entry -> entries.remove(entry));
		return optionalEntry.orElse(null);
	}

}
