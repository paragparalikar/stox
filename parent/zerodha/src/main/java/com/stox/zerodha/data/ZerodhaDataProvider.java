package com.stox.zerodha.data;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stox.core.intf.Callback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.repository.BarRepository;
import com.stox.core.repository.InstrumentRepository;
import com.stox.core.ui.widget.modal.Modal;
import com.stox.data.DataProvider;
import com.stox.data.tick.TickConsumer;
import com.stox.data.tick.TickConsumerRegistry;
import com.stox.zerodha.Zerodha;

@Component
public class ZerodhaDataProvider extends Zerodha implements DataProvider {

	@Autowired
	private InstrumentRepository instrumentRespository;

	@Autowired
	private BarRepository barRepository;
	
	private final TickConsumerRegistry tickConsumerRegistry = new TickConsumerRegistry();

	@Override
	public void register(TickConsumer consumer) {
		synchronized (tickConsumerRegistry) {
			tickConsumerRegistry.register(consumer);
		}
	}

	@Override
	public void unregister(TickConsumer consumer) {
		synchronized (tickConsumerRegistry) {
			tickConsumerRegistry.unregister(consumer);
		}
	}

	@Override
	public String getCode() {
		return "zerodha";
	}

	@Override
	public String getName() {
		return "Zerodha";
	}

	@Override
	public void login(final Callback<Void, Void> callback) throws Throwable {
		final Modal modal = new Modal();
		modal.addStylesheets("styles/zerodha.css");
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

	/*
	 * private void loadInstruments() throws Exception { if (null == instruments || instruments.isEmpty()) { if
	 * (DateUtil.isToday(instrumentRespository.getLastUpdatedDate(getCode()))) { instruments = instrumentRespository.getAllInstruments(getCode()); } else { try { instruments =
	 * doGetInstruments(); instrumentRespository.save(getCode(), instruments); } catch (Exception e) { instruments = instrumentRespository.getAllInstruments(getCode()); } } final
	 * Date today = new Date(); instruments = instruments.stream().filter(instrument -> { final String symbol = instrument.getSymbol(); final Date expiry = instrument.getExpiry();
	 * if (null != expiry && expiry.before(today)) { return false; } return null != symbol && 3 < symbol.length() && '-' != symbol.charAt(symbol.length() - 3); }).sorted(new
	 * HasName.HasNameComaparator<>()).collect(Collectors.toList()); cache = instruments.stream().collect(Collectors.toMap(Instrument::getId, Function.identity(), (p1, p2) -> p1));
	 * } }
	 * 
	 * private List<Instrument> doGetInstruments() throws Exception { final HttpResponse response = HttpClientBuilder.create().build().execute(new
	 * HttpGet("https://api.kite.trade/instruments")); final CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build(); final MappingIterator<ZerodhaInstrument> iterator
	 * = Constant.csvMapper.readerFor(ZerodhaInstrument.class).with(csvSchema).readValues(response.getEntity().getContent()); final List<Instrument> instruments =
	 * iterator.readAll().stream().map(zerodhaInstrument -> zerodhaInstrument.toInstrument()).collect(Collectors.toList()); instruments.sort(new HasNameComaparator<>()); return
	 * instruments; }
	 */

	// @Secured
	@Override
	public List<Bar> getBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to) {
		return barRepository.find(instrument.getId(), barSpan, from, to);
	}
}
