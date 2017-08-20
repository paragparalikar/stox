package com.stox.data;

import lombok.Value;

import com.stox.core.model.Bar;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;

@Value
public class BarTopic {

	public static interface Listener {

		public void onBar(final Bar bar);

	}

	private Instrument instrument;

	private BarSpan barSpan;

}
