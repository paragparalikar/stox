package com.stox.chart;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public interface MouseHandler extends EventHandler<MouseEvent> {

	void attach();

	void detach();

}
