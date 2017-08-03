package com.stox.core.ui.widget;

import java.util.LinkedList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.stox.core.ui.ResizableRelocatableStageDecorator;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.StringUtil;

public class Toast extends Stage {

	private static final int SPACING = 20;
	private static final List<Toast> TOASTS = new LinkedList<>();

	private final Label titleLabel = new Label();
	private final HBox titleBar = UiUtil.classes(new HBox(titleLabel), "title");
	private final Label messageLabel = UiUtil.classes(new Label(), "message");
	private final Button closeButton = UiUtil.classes(new Button(Icon.CROSS), "icon");
	private final BorderPane root = UiUtil.classes(new BorderPane(), "toast");
	private final Scene scene = new Scene(root);
	private final ResizableRelocatableStageDecorator decorator = new ResizableRelocatableStageDecorator(this);

	public Toast(final String message) {
		this(null, message);
	}

	public Toast(final String title, final String message) {
		this(title, message, null);
	}

	public Toast(final String title, final String message, final Node node) {
		this(title, message, node, true);
	}

	public Toast(final String title, final String message, final Node node, final boolean closeable) {
		this(title, message, node, closeable, false);
	}

	public Toast(final String title, final String message, final Node node, final boolean closeable, final boolean autoClose) {
		setScene(scene);
		initStyle(StageStyle.TRANSPARENT);

		decorator.bindTitleBar(root);
		closeButton.addEventHandler(ActionEvent.ACTION, event -> hide());
		setOnShowing(event -> {
			TOASTS.add(this);
			centerOnScreen();
		});
		setOnHidden(event -> {
			TOASTS.remove(this);
		});
		scene.getStylesheets().addAll("styles/color-sceme.css", "fonts/open-sans/open-sans.css", "styles/bootstrap.css", "styles/common.css", "styles/toast.css");

		if (StringUtil.hasText(title)) {
			titleLabel.setText(title);
			root.setTop(titleBar);
		}

		if (closeable) {
			titleBar.getChildren().add(closeButton);
			if (null == root.getTop()) {
				root.setTop(titleBar);
			}
		}

		if (StringUtil.hasText(message)) {
			messageLabel.setText(message);
			root.setCenter(messageLabel);
		}

		if (null != node) {
			if (null == root.getCenter()) {
				root.setCenter(node);
			} else {
				root.setBottom(node);
			}
		}

	}

}
