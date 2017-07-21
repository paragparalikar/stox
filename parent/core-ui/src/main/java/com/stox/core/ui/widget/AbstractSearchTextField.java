package com.stox.core.ui.widget;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.AbstractSearchTextField.Callback;
import com.sun.javafx.scene.control.skin.TextFieldSkin;

public abstract class AbstractSearchTextField<T> extends TextField {

	public static interface Callback<T> {
		boolean call(final T item, final String text);
	}

	private final Button next = UiUtil.classes(new Button(Icon.ARROW_DOWN), "icon", "text-field-button");
	private final Button previous = UiUtil.classes(new Button(Icon.ARROW_UP), "icon", "text-field-button");
	private final Button clear = UiUtil.classes(new Button(Icon.CROSS), "icon", "text-field-button");
	private final HBox right = UiUtil.classes(UiUtil.box(new HBox(next, previous, clear)), "right");
	private Callback<T> matcher;

	public AbstractSearchTextField() {
		this(null);
	}

	public AbstractSearchTextField(final AbstractSearchTextField.Callback<T> matcher) {
		this.matcher = null != matcher ? matcher : new DefaultMatcher<>();
		getChildren().add(right);
		HBox.setHgrow(this, Priority.ALWAYS);
		setMaxWidth(Double.MAX_VALUE);
		getStyleClass().add("search-field");

		setPromptText("Search");

		next.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				next();
			}
		});

		previous.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				previous();
			}
		});

		clear.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				clear();
			}
		});

		addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (KeyCode.ENTER.equals(event.getCode()) || KeyCode.DOWN.equals(event.getCode())) {
					next();
				} else if (KeyCode.UP.equals(event.getCode())) {
					previous();
				}
			}
		});
	}

	public void setMatcher(Callback<T> matcher) {
		this.matcher = null == matcher ? this.matcher : matcher;
	}

	public Callback<T> getMatcher() {
		return matcher;
	}

	public abstract void previous();

	public abstract void next();

	@Override
	protected Skin<?> createDefaultSkin() {
		return new TextFieldSkin(this) {

			@Override
			protected void layoutChildren(double x, double y, double w, double h) {
				final double fullHeight = h + snappedTopInset() + snappedBottomInset();

				final double leftWidth = 0;
				final double rightWidth = snapSize(right.prefWidth(fullHeight));

				final double textFieldStartX = snapPosition(x) + snapSize(leftWidth);
				final double textFieldWidth = w - snapSize(leftWidth) - snapSize(rightWidth);

				super.layoutChildren(textFieldStartX, 0, textFieldWidth, fullHeight);

				final double rightStartX = w - rightWidth + snappedLeftInset();
				right.resizeRelocate(rightStartX, 0, rightWidth, fullHeight);
			}

			@Override
			protected double computePrefWidth(double h, double topInset, double rightInset, double bottomInset, double leftInset) {
				final double pw = super.computePrefWidth(h, topInset, rightInset, bottomInset, leftInset);
				final double leftWidth = 0;
				final double rightWidth = snapSize(right.prefWidth(h));

				return pw + leftWidth + rightWidth + leftInset + rightInset;
			}

			@Override
			protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
				return computePrefWidth(height, topInset, rightInset, bottomInset, leftInset);
			}

		};
	}

}

class DefaultMatcher<T> implements Callback<T> {

	@Override
	public boolean call(final T item, final String text) {
		return String.valueOf(item).toLowerCase().contains(String.valueOf(text).toLowerCase());
	}

}