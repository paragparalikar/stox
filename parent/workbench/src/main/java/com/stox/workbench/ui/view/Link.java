package com.stox.workbench.ui.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import lombok.Value;

import com.stox.core.model.BarSpan;

public enum Link {

	BROWN(Color.BROWN), MAGENTA(Color.DARKMAGENTA), RED(Color.RED), GREEN(Color.GREEN), BLUE(Color.BLUE);

	public static final State DEFAULT = new State(null, BarSpan.D, 0);

	public static interface HasState {

		public State getState();

		public void setState(final State state);

	}

	@Value
	public static class State {

		private final String instrumentCode;
		private final BarSpan barSpan;
		private final long date;

		public State copyWithInstrumentCode(final String instrumentCode) {
			return new State(instrumentCode, barSpan, date);
		}

		public State copyWithBarSpan(final BarSpan barSpan) {
			return new State(instrumentCode, barSpan, date);
		}

		public State copyWithDate(final long date) {
			return new State(instrumentCode, barSpan, date);
		}

	}

	private final Color color;
	private final ObjectProperty<State> stateProperty = new SimpleObjectProperty<State>(new State(null, BarSpan.D, 0));

	private Link(final Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public State getState() {
		return stateProperty.get();
	}

	public void setState(final State state) {
		stateProperty.set(null == state ? DEFAULT : state);
	}

	public ReadOnlyObjectProperty<State> stateProperty() {
		return stateProperty;
	}

}
