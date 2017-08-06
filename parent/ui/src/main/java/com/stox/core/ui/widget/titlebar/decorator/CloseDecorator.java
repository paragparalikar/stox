package com.stox.core.ui.widget.titlebar.decorator;

import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.Button;

import com.stox.core.intf.HasLifecycle;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.titlebar.TitleBar;

public class CloseDecorator {

	private HasLifecycle hasLifecycle;
	private final Button closeButton = UiUtil.classes(new Button(Icon.CROSS), "icon");

	public CloseDecorator() {
		closeButton.addEventHandler(ActionEvent.ACTION, event -> hasLifecycle.stop());
	}

	public void setTitleBar(final TitleBar titleBar) {
		titleBar.add(Side.RIGHT, titleBar.getChildCount(Side.RIGHT), closeButton);
	}

	public void setHasLifecycle(HasLifecycle hasLifecycle) {
		this.hasLifecycle = hasLifecycle;
	}

}
