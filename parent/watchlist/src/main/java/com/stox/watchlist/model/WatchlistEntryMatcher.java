package com.stox.watchlist.model;

import com.stox.core.ui.widget.AbstractSearchTextField.Callback;
import com.stox.core.util.StringUtil;

public class WatchlistEntryMatcher implements Callback<WatchlistEntry>{

	@Override
	public boolean call(WatchlistEntry item, String text) {
		return null == item || null == item.getInstrument() || !StringUtil.hasText(text) ? false : item.getInstrument().getName().trim().toLowerCase().contains(text.toLowerCase().trim());
	}
}
