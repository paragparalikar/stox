package com.stox.core.ui.widget.titlebar.decorator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Side;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.AbstractSearchTextField;
import com.stox.core.ui.widget.titlebar.TitleBar;

public class SearchDecorator<T> {

	private final ToggleButton button = new ToggleButton(Icon.SEARCH);
	private AbstractSearchTextField<T> textField;
	private final HBox container = new HBox();
	private final Pane pane = UiUtil.classes(UiUtil.fullArea(new HBox()), "panel-sm");

	private class SearchToggleListener implements ChangeListener<Boolean> {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean selected) {
			if (null != textField && !pane.getChildren().contains(textField)) {
				pane.getChildren().add(textField);
			}

			if (selected && !container.getChildren().contains(pane)) {
				container.getChildren().add(pane);
			}
			if (!selected) {
				container.getChildren().remove(pane);
			}
		}
	}

	public SearchDecorator() {
		button.getStyleClass().addAll("fa", "primary");
	}

	public void setTitleBar(final TitleBar titleBar) {
		titleBar.add(Side.RIGHT, 0, button);
		titleBar.add(Side.BOTTOM, 0, container);
		button.selectedProperty().addListener(new SearchToggleListener());
	}

	public void setSearchTextField(AbstractSearchTextField<T> searchTextField) {
		this.textField = searchTextField;
	}

}
