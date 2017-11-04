package com.stox.screener.selection;

import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.screen.Screen;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.Getter;

@Getter
public class ScreenSelectionView extends BorderPane {

	private final Label titleLabel = UiUtil.classes(new Label("Select Screen"));
	private final ListView<Screen> listView = new ListView<>();
	private final Button nextButton = UiUtil.classes(new Button(Icon.ARROW_RIGHT), "icon", "primary");
	private final HBox buttonBar = UiUtil.classes(new HBox(UiUtil.spacer(), nextButton), "button-group", "right");
	
	public ScreenSelectionView() {
		setTop(titleLabel);
		setBottom(buttonBar);
		setCenter(listView);
	}
	
}
