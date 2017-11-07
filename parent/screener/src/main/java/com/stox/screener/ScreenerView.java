package com.stox.screener;

import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.view.View;

import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;

@Getter
public class ScreenerView extends View{
	
	private final Label titleLabel = UiUtil.classes(new Label(), "h1");
	private final Button previousButton = UiUtil.classes(new Button(Icon.ARROW_LEFT), "icon", "primary");
	private final Button nextButton = UiUtil.classes(new Button(Icon.ARROW_RIGHT), "icon", "primary");
	private final HBox buttonBar = UiUtil.classes(new HBox(previousButton, UiUtil.spacer(), nextButton), "button-group", "right");

	public ScreenerView() {
		super(ScreenerUiConstant.CODE, ScreenerUiConstant.NAME, ScreenerUiConstant.ICON);
		getTitleBar().add(Side.BOTTOM, 0, titleLabel);
		setButtonBar(buttonBar);
	}

}
