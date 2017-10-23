package com.stox.core.ui.widget.modal;

import java.util.List;

import com.stox.core.intf.HasLifecycle;
import com.stox.core.model.Message;
import com.stox.core.ui.HasSpinner;
import com.stox.core.ui.ResizableRelocatableWindowDecorator;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.ApplicationStage;
import com.stox.core.ui.widget.MessagePane;
import com.stox.core.ui.widget.titlebar.TitleBar;
import com.stox.core.ui.widget.titlebar.decorator.CloseDecorator;
import com.stox.core.util.StringUtil;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Modal implements HasLifecycle, HasSpinner {

	private final TitleBar titleBar = new TitleBar();
	private final VBox top = new VBox(titleBar.getNode());
	private final CloseDecorator closeDecorator = new CloseDecorator();
	private final VBox container = new VBox();
	private final BorderPane borderPane = new BorderPane(container, top, null, null, null);
	private final StackPane root = new StackPane(borderPane);
	private final Stage stage = new Stage();
	private final VBox spinner = UiUtil.classes(new VBox(new ProgressIndicator()), "center");
	private final ResizableRelocatableWindowDecorator stageDecorator = new ResizableRelocatableWindowDecorator(stage);
	
	private Runnable onHide;

	public Modal() {
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setAlwaysOnTop(true);
		closeDecorator.setTitleBar(titleBar);
		closeDecorator.setHasLifecycle(this);

		root.getStyleClass().add("modal");
		container.getStyleClass().add("container");

		stageDecorator.bindTitleBar(titleBar.getNode());
		final Scene scene = new Scene(root);
		scene.getStylesheets().addAll("styles/color-sceme.css", "fonts/open-sans/open-sans.css", "styles/bootstrap.css", "styles/common.css", "styles/workbench.css",
				"styles/modal.css");
		stage.setScene(scene);
	}
	
	public void onHide(final Runnable runnable) {
		this.onHide = runnable;
	}
	
	public void setMessage(final Message message) {
		clearMessages();
		addMessage(message);
	}

	public void addMessage(final Message message) {
		if (null != message && null != message.getType() && StringUtil.hasText(message.getText())) {
			final MessagePane messagePane = new MessagePane();
			top.getChildren().add(messagePane);
			messagePane.setMessage(message);
		} else {
			clearMessages();
		}
	}

	public void clearMessages() {
		top.getChildren().removeIf(node -> node instanceof MessagePane);
	}

	@Override
	public void showSpinner(boolean value) {
		Platform.runLater(() -> {
			root.setDisable(value);
			if (value && !root.getChildren().contains(spinner)) {
				root.getChildren().add(spinner);
			} else if (!value) {
				root.getChildren().remove(spinner);
			}
		});
	}

	public void setContent(final Node node) {
		container.getChildren().clear();
		VBox.setVgrow(node, Priority.ALWAYS);
		node.getStyleClass().add("content");
		container.getChildren().add(node);
	}

	public void setButtonGroup(final Node node) {
		borderPane.setBottom(node);
	}

	public List<String> getStyleClass() {
		return root.getStyleClass();
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {
		hide();
	}

	public void show() {
		final ApplicationStage workbench = ApplicationStage.getInstance();
		if (null != workbench) {
			workbench.showGlass(true);
		}
		stage.show();
	}

	public void hide() {
		final ApplicationStage workbench = ApplicationStage.getInstance();
		if (null != workbench) {
			workbench.showGlass(false);
		}
		if(null != onHide) {
			onHide.run();
		}
		stage.hide();
	}

	public void addStylesheets(final String... path) {
		stage.getScene().getStylesheets().addAll(path);
	}

	public void setTitle(final String title) {
		titleBar.setTitleText(title);
	}

	protected TitleBar getTitleBar() {
		return titleBar;
	}

}
