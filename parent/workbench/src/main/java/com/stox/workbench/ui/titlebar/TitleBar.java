package com.stox.workbench.ui.titlebar;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import com.stox.core.ui.BoxChildrenChangeListener;
import com.stox.core.ui.util.UiUtil;

public class TitleBar {

	private final Label titleLabel = UiUtil.classes(new Label(), "title-label", "primary");
	private final HBox left = new HBox(titleLabel);
	private final HBox right = new HBox();
	private final HBox hContainer = new HBox(left, UiUtil.spacer(), right);
	private final VBox top = UiUtil.classes(new VBox(), "title-top");
	private final VBox bottom = UiUtil.classes(new VBox(), "title-bottom");
	private final VBox vContainer = UiUtil.classes(new VBox(top, hContainer, bottom), "title");

	public TitleBar() {
		right.getChildren().addListener(BoxChildrenChangeListener.getInstance());
	}

	public Node getNode() {
		return vContainer;
	}

	public Node getMovableNode() {
		return hContainer;
	}

	public TitleBar setTitleText(final String text) {
		titleLabel.setText(text);
		return this;
	}

	public TitleBar setGraphic(final String graphic) {
		titleLabel.setGraphic(UiUtil.graphic(graphic));
		return this;
	}

	private Pane getSide(final Side side) {
		switch (side) {
		case BOTTOM:
			return bottom;
		case LEFT:
			return left;
		case RIGHT:
			return right;
		case TOP:
			return top;
		}
		return null;
	}

	public TitleBar add(final Side side, final int index, final Node node) {
		getSide(side).getChildren().add(index, node);
		return this;
	}

	public int getChildCount(final Side side) {
		return getSide(side).getChildren().size();
	}

}
