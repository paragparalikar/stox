package com.stox.screener.execution;

import com.stox.core.model.Instrument;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.Getter;

@Getter
public class ScreenExecutionView extends BorderPane {

	private final ListView<Instrument> matches = new ListView<>();
	private final ProgressBar progressBar = new ProgressBar(); 
	private final Button actionButton = UiUtil.classes(new Button(Icon.PLAY), "icon","primary");
	private final HBox actionPane = UiUtil.classes(new HBox(progressBar, actionButton),"container");
	
	public ScreenExecutionView() {
		setCenter(matches);
		setBottom(actionPane);
	}
	
	public void updateProgress(final double progress) {
		Platform.runLater(() -> progressBar.setProgress(progress));
	}
}
