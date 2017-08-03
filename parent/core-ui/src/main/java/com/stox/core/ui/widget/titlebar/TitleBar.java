package com.stox.core.ui.widget.titlebar;

import javafx.collections.ListChangeListener;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import com.stox.core.ui.BoxChildrenChangeListener;
import com.stox.core.ui.util.UiUtil;

public class TitleBar {

	private final Label titleLabel = UiUtil.fullWidth(new Label());
	private final HBox left = UiUtil.classes(new HBox(), "left");
	private final HBox right = UiUtil.classes(new HBox(), "right");
	private final HBox hContainer = UiUtil.classes(new HBox(titleLabel, UiUtil.spacer()), "center");
	private final VBox top = UiUtil.classes(new VBox(), "top");
	private final VBox bottom = UiUtil.classes(new VBox(), "bottom");
	private final VBox vContainer = UiUtil.classes(new VBox(hContainer), "title", "center");

	public TitleBar() {
		right.getChildren().addListener(BoxChildrenChangeListener.getInstance());
		left.getChildren().addListener((ListChangeListener<Node>) change -> {
			if (0 == left.getChildren().size()) {
				hContainer.getChildren().remove(left);
			} else if (!hContainer.getChildren().contains(left)) {
				hContainer.getChildren().add(0, left);
			}
		});
		right.getChildren().addListener((ListChangeListener<Node>) change -> {
			if (0 == right.getChildren().size()) {
				hContainer.getChildren().remove(right);
			} else if (!hContainer.getChildren().contains(right)) {
				hContainer.getChildren().add(right);
			}
		});
		top.getChildren().addListener((ListChangeListener<Node>) change -> {
			if (0 == top.getChildren().size()) {
				vContainer.getChildren().remove(top);
			} else if (!vContainer.getChildren().contains(top)) {
				vContainer.getChildren().add(0, top);
			}
		});
		bottom.getChildren().addListener((ListChangeListener<Node>) change -> {
			if (0 == bottom.getChildren().size()) {
				vContainer.getChildren().remove(bottom);
			} else if (!vContainer.getChildren().contains(bottom)) {
				vContainer.getChildren().add(bottom);
			}
		});
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

	public void remove(final Side side, final Node node) {
		getSide(side).getChildren().remove(node);
	}

	public int getChildCount(final Side side) {
		return getSide(side).getChildren().size();
	}

	public void clear(final Side side) {
		getSide(side).getChildren().clear();
	}

}
