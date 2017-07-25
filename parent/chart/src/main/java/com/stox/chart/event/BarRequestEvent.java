package com.stox.chart.event;

import java.util.Date;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.EqualsAndHashCode;
import lombok.Value;

import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;

@Value
@EqualsAndHashCode(callSuper = true)
public class BarRequestEvent extends Event {

	private static final long serialVersionUID = -6936781477710622582L;
	public static final EventType<BarRequestEvent> TYPE = new EventType<>("barRequestEvent");

	private final String instrumentId;
	private final BarSpan barSpan;
	private final Date from;
	private final Date to;
	private final ResponseCallback<List<Bar>> callback;

	public BarRequestEvent(final String exchangeCode, final BarSpan barSpan, final Date from, final Date to, final ResponseCallback<List<Bar>> callback) {
		super(TYPE);
		this.to = to;
		this.from = from;
		this.barSpan = barSpan;
		this.callback = callback;
		this.instrumentId = exchangeCode;
	}

}
