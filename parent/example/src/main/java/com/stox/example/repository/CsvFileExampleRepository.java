package com.stox.example.repository;

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
import com.stox.example.model.Example;
import com.stox.example.model.ExampleExistsException;

@Lazy
@Component
public class CsvFileExampleRepository implements ExampleRepository {

	@Autowired
	private InstrumentRepository instrumentRepository;

	private final CsvSchema schema = Constant.csvMapper.schemaFor(Example.class).withHeader();

	@Override
	public List<Example> load(Integer exampleGroupId) throws Exception {
		final File file = FileUtil.getFile(ExampleRepositoryUtil.getExampleGroupFilePath(exampleGroupId));
		final List<Example> examples = Constant.csvMapper.reader(schema).forType(Example.class)
				.<Example>readValues(file).readAll();
		examples.forEach(
				example -> example.setInstrument(instrumentRepository.getInstrument(example.getInstrumentId())));
		return examples;
	}

	@Override
	public Example save(Example example) throws Exception {
		final List<Example> examples = load(example.getExampleGroupId());
		if (!StringUtil.hasText(example.getId())) {
			final Predicate<Example> predicate = e -> e.getInstrumentId().equals(example.getInstrumentId())
					&& e.getBarSpan().equals(example.getBarSpan()) && e.getDate().equals(example.getDate());
			examples.stream().filter(predicate).findFirst().ifPresent(e -> {
				throw new ExampleExistsException(example);
			});
			example.setId(UUID.randomUUID().toString());
		} else {
			examples.removeIf(e -> e.getId().equals(example.getId()));
		}
		examples.add(example);
		final File file = FileUtil.getFile(ExampleRepositoryUtil.getExampleGroupFilePath(example.getExampleGroupId()));
		Constant.csvMapper.writer(schema).writeValues(file).writeAll(examples).flush();
		return example;
	}

	@Override
	public Example delete(final Integer exampleGroupId, final String exampleId) throws Exception {
		final List<Example> examples = load(exampleGroupId);
		final Optional<Example> optionalEntry = examples.stream().filter(example -> example.getId().equals(exampleId))
				.findFirst();
		optionalEntry.ifPresent(example -> examples.remove(example));
		return optionalEntry.orElse(null);
	}

}
