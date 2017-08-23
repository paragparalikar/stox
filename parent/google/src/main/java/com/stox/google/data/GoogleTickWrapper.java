package com.stox.google.data;

import java.util.List;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.data.tick.TickWrapper;

import lombok.Value;

@Value
public class GoogleTickWrapper implements TickWrapper {
	
	private Bar tick;
	
	private BarSpan barSpan;
	
	private Instrument instrument;
	
	@Override
	public boolean mergeWith(List<Bar> bars) {
		if(null != bars && null != barSpan && null != tick) {
			if(bars.isEmpty()) {
				return bars.add(tick);
			}else {
				final Bar bar = bars.get(0);
				if(tick.getDate().getTime() >= barSpan.next(bar.getDate().getTime())) {
					bars.add(0, tick);
					return true;
				}else if(tick.getDate().getTime() >= bar.getDate().getTime()){
					bar.setHigh(tick.getHigh());
					bar.setLow(tick.getLow());
					bar.setClose(tick.getClose());
					
					/* In case of Google Finance data provider, we are downloading an entire bar for
					 * given barSpan, not a real tick which is last price and last trade size.
					 * Thus in this case whole bar volume is to be substituted with tick volume, instead of adding it to existing volume. */
					bar.setVolume(bar.getVolume());
					return false;
				}
			}
		}
		return false;
	}

}
