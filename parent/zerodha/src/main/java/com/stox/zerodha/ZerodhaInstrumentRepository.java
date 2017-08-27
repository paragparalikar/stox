package com.stox.zerodha;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.stox.core.model.Instrument;
import com.stox.core.util.Constant;
import com.stox.core.util.DateUtil;
import com.stox.core.util.StringUtil;
import com.stox.zerodha.model.ZerodhaExchange;
import com.stox.zerodha.model.ZerodhaInstrument;
import com.stox.zerodha.model.ZerodhaInstrumentType;
import com.stox.zerodha.model.ZerodhaSegment;

@Component
public class ZerodhaInstrumentRepository {
	private static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	private final Map<String, ZerodhaInstrument> cache = new HashMap<>();

	private String getPath() {
		return Constant.PATH + "zerodha" + File.separator + "com.stox.zerodha.instruments.csv";
	}

	private synchronized void load() throws Exception {
		if (cache.isEmpty()) {
			final File file = new File(getPath());
			if (!file.exists()
					|| new Date(Files.getLastModifiedTime(file.toPath(), LinkOption.NOFOLLOW_LINKS).toMillis())
							.before(DateUtil.trim(new Date()))) {

				file.getParentFile().mkdirs();
				Files.deleteIfExists(file.toPath());
				file.createNewFile();

				try(final FileOutputStream fileOutputStream = new FileOutputStream(file, false)){
					final URL url = new URL("https://api.kite.trade/instruments");
					final ReadableByteChannel rbc = Channels.newChannel(url.openStream());
					fileOutputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				}
			} 
			Files.readAllLines(file.toPath()).forEach(line -> {
				final ZerodhaInstrument instrument = parse(line);
				if(null != instrument) {
					cache.put(instrument.getTradingsymbol(), instrument);
				}
			});
		}
	}

	
	private ZerodhaInstrument parse(final String text) {
		try {
			final String[] tokens = text.split(",");
			if("expiry".equals(tokens[5]) || "0.0".equals(tokens[5])) {
				return null;
			}
			final ZerodhaInstrument instrument = new ZerodhaInstrument();
			instrument.setInstrumentToken(tokens[0]);
			instrument.setExchangeToken(tokens[1]);
			instrument.setTradingsymbol(tokens[2]);
			instrument.setName(tokens[3]);
			instrument.setLastPrice(StringUtil.parseDouble(tokens[4]));
			instrument.setExpiry(StringUtil.hasText(tokens[5]) ? DATEFORMAT.parse(tokens[5]) : null);
			instrument.setStrike(StringUtil.parseDouble(tokens[6]));
			instrument.setTickSize(StringUtil.parseDouble(tokens[7]));
			instrument.setLotSize(StringUtil.parseInt(tokens[8]));
			instrument.setInstrumentType(ZerodhaInstrumentType.findByCode(tokens[9]));
			instrument.setSegment(ZerodhaSegment.findByCode(tokens[10]));
			instrument.setExchange(ZerodhaExchange.findByCode(tokens[11]));
			return instrument;
		}catch(final Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
	
	public ZerodhaInstrument findByInstrument(final Instrument instrument) throws Exception {
		return findBySymbol(instrument.getSymbol());
	}

	public ZerodhaInstrument findBySymbol(final String symbol) throws Exception {
		load();
		return cache.get(symbol);
	}

}
