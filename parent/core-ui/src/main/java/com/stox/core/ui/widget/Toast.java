package com.stox.core.ui.widget;

import java.util.LinkedList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.stox.core.ui.ResizableRelocatableStageDecorator;
import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;

public class Toast extends Stage {

	private static final int SPACING = 20;
	private static final List<Toast> TOASTS = new LinkedList<>();

	private final Label messageLabel = UiUtil.classes(new Label(), "message");
	private final Button closeButton = UiUtil.classes(new Button(Icon.CROSS), "icon");
	private final VBox buttonContainer = UiUtil.classes(new VBox(closeButton, UiUtil.spacer()), "button-container");
	private final VBox messageContainer = UiUtil.classes(UiUtil.fullWidth(new VBox(messageLabel)), "message-container");
	private final HBox container = UiUtil.classes(new HBox(messageContainer, buttonContainer), "container");
	private final StackPane root = UiUtil.classes(new StackPane(container), "toast");
	private final Scene scene = new Scene(root);
	private final ResizableRelocatableStageDecorator decorator = new ResizableRelocatableStageDecorator(this);

	public Toast(final String message) {
		this("primary", message);
	}

	public Toast(final String style, final String message) {
		this(style, message, null);
	}

	public Toast(final String style, final String message, final Node node) {
		setScene(scene);
		initStyle(StageStyle.TRANSPARENT);
		root.getStyleClass().add(style);

		if (null != node) {
			messageContainer.getChildren().add(node);
		}

		messageLabel.setText(message);

		decorator.bindTitleBar(messageContainer);
		closeButton.addEventHandler(ActionEvent.ACTION, event -> hide());
		setOnShowing(event -> {
			TOASTS.add(this);
			centerOnScreen();
		});
		setOnHidden(event -> {
			TOASTS.remove(this);
		});
		scene.getStylesheets().addAll("styles/color-sceme.css", "fonts/open-sans/open-sans.css", "styles/bootstrap.css", "styles/common.css", "styles/toast.css");

	}

}
