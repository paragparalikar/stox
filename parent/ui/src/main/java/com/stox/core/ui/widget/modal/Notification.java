package com.stox.core.ui.widget.modal;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.ResizeRelocateDecorator;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.ApplicationStage;

public class Notification {

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean defaultPositionHandled = false;
	private final Button closeButton = UiUtil.classes(new Button(Icon.CROSS), "icon", "top");
	private final VBox right = UiUtil.classes(new VBox(closeButton, UiUtil.spacer()), "right", "top");
	private final BorderPane container = UiUtil.classes(new BorderPane(), "notification");
	@Getter(AccessLevel.NONE)
	private final ApplicationStage applicationStage = ApplicationStage.getInstance();

	private final Node graphic;
	private final String style;

	@Builder
	private Notification(final String style, final Node graphic) {
		this.style = style;
		this.graphic = graphic;
	}

	public void show() {
		if (Platform.isFxApplicationThread()) {
			doShow();
		} else {
			Platform.runLater(() -> doShow());
		}
	}

	private void doShow() {
		UiUtil.classes(container, style);
		UiUtil.classes(graphic, "graphic");
		container.setRight(right);
		container.setCenter(graphic);
		new ResizeRelocateDecorator(container);
		container.layoutBoundsProperty().addListener((observable, old, bounds) -> {
			handleDefaultPosition();
		});
		closeButton.addEventHandler(ActionEvent.ACTION, event -> hide());
		applicationStage.showModal(container);
	}

	private void handleDefaultPosition() {
		if (!defaultPositionHandled) {
			defaultPositionHandled = true;
			container.setLayoutY(100);
			container.setLayoutX(applicationStage.getX() + applicationStage.getWidth() - container.getWidth() - 20);
		}
	}

	public void hide() {
		if (Platform.isFxApplicationThread()) {
			doHide();
		} else {
			Platform.runLater(() -> doHide());
		}
	}

	private void doHide() {
		applicationStage.hideModal(container);
	}
}
