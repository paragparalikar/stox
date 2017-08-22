package com.stox.watchlist.ui;

import com.stox.watchlist.model.Watchlist;

import javafx.util.StringConverter;

public class WatchlistStringConverter extends StringConverter<Watchlist> {

	@Override
	public String toString(Watchlist watchlist) {
		return null == watchlist ? "" : watchlist.getName();
	}

	@Override
	public Watchlist fromString(String string) {
		return null;
	}

}
