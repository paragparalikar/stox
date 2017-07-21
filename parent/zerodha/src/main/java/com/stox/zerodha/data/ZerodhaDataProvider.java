package com.stox.zerodha.data;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.client.Secured;
import com.stox.core.intf.Callback;
import com.stox.core.intf.HasName;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.util.Constant;
import com.stox.core.util.DateUtil;
import com.stox.data.DataProvider;
import com.stox.workbench.ui.modal.Modal;
import com.stox.zerodha.Zerodha;
import com.stox.zerodha.ui.ZerodhaInstrumentFilterPresenter;

@Component
public class ZerodhaDataProvider extends Zerodha implements DataProvider {

	private List<Instrument> instruments;
	private Map<String, Instrument> cache;

	@Autowired
	private InstrumentRepository instrumentRespository;

	@Override
	public String getCode() {
		return "zerodha";
	}

	@Override
	public String getName() {
		return "Zerodha";
	}

	@Override
	public Modal getInstrumentFilterView(final List<Instrument> target) {
		final ZerodhaInstrumentFilterPresenter presenter = new ZerodhaInstrumentFilterPresenter(instruments, target);
		return presenter.getView();
	}

	@Override
	public void login(final Callback<Void, Void> callback) throws Throwable {
		final Modal modal = new Modal();
		modal.addStylesheet("styles/zerodha.css");
		modal.getStyleClass().add("zerodha-login-modal");
		modal.setTitle("Login to Zerodha"); // TODO I18N here
		modal.setContent(createLoginView(p -> {
			modal.hide();
			callback.call(null);
			return null;
		}));
		modal.show();
	}

	@Override
	public boolean isLoggedIn() {
		return null != getCookies();
	}

	@Override
	public Instrument getInstrument(String code) throws Exception {
		loadInstruments();
		return cache.get(code);
	}

	@Override
	public List<Instrument> getInstruments() throws Exception {
		loadInstruments();
		return instruments;
	}

	private void loadInstruments() throws Exception {
		if (null == instruments || instruments.isEmpty()) {
			if (DateUtil.isToday(instrumentRespository.getLastUpdatedDate(getCode()))) {
				instruments = instrumentRespository.getAllInstruments(getCode());
			} else {
				instruments = doGetInstruments();
				instrumentRespository.save(getCode(), instruments);
			}
			instruments = instruments.stream().filter(instrument -> {
				final String symbol = instrument.getSymbol();
				return null != symbol && 3 < symbol.length() && '-' != symbol.charAt(symbol.length() - 3);
			}).sorted(new HasName.HasNameComaparator<>()).collect(Collectors.toList());
			cache = instruments.stream().collect(Collectors.toMap(Instrument::getId, Function.identity(), (p1, p2) -> p1));
		}
	}

	private List<Instrument> doGetInstruments() throws Exception {
		final HttpResponse response = HttpClientBuilder.create().build().execute(new HttpGet("https://api.kite.trade/instruments"));
		final CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		final MappingIterator<ZerodhaInstrument> iterator = Constant.csvMapper.readerFor(ZerodhaInstrument.class).with(csvSchema).readValues(response.getEntity().getContent());
		final List<Instrument> instruments = iterator.readAll().stream().map(zerodhaInstrument -> zerodhaInstrument.toInstrument()).collect(Collectors.toList());
		instruments.sort(new HasNameComaparator<>());
		return instruments;
	}

	@Secured
	@Override
	public List<Bar> getBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to) {
		// TODO Auto-generated method stub
		return null;
	}

}
