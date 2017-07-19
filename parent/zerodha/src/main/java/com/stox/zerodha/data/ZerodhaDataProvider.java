package com.stox.zerodha.data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.stox.core.client.Secured;
import com.stox.core.intf.Callback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.util.Constant;
import com.stox.data.DataProvider;
import com.stox.workbench.ui.modal.Modal;
import com.stox.zerodha.Zerodha;

@Component
public class ZerodhaDataProvider extends Zerodha implements DataProvider {

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
	public List<Instrument> getInstruments() throws Exception {
		final HttpResponse response = HttpClientBuilder.create().build().execute(new HttpGet("https://api.kite.trade/instruments"));
		final CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		final MappingIterator<ZerodhaInstrument> iterator = Constant.csvMapper.readerFor(ZerodhaInstrument.class).with(csvSchema).readValues(response.getEntity().getContent());
		return iterator.readAll().stream().map(zerodhaInstrument -> zerodhaInstrument.toInstrument()).collect(Collectors.toList());
	}

	@Secured
	@Override
	public List<Bar> getBars(final Instrument instrument, final BarSpan barSpan, final Date from, final Date to) {
		// TODO Auto-generated method stub
		return null;
	}

}
