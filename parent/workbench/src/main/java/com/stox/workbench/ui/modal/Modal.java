package com.stox.workbench.ui.modal;

import java.util.List;

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

import com.stox.core.intf.HasLifecycle;
import com.stox.core.ui.HasSpinner;
import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.stage.ResizableRelocatableStageDecorator;
import com.stox.workbench.ui.stage.Workbench;
import com.stox.workbench.ui.titlebar.TitleBar;
import com.stox.workbench.ui.titlebar.decorator.CloseDecorator;

public class Modal implements HasLifecycle, HasSpinner {

	private final TitleBar titleBar = new TitleBar();
	private final CloseDecorator closeDecorator = new CloseDecorator();
	private final VBox container = new VBox();
	private final BorderPane borderPane = new BorderPane(container, titleBar.getNode(), null, null, null);
	private final StackPane root = new StackPane(borderPane);
	private final Stage stage = new Stage();
	private final VBox spinner = UiUtil.classes(new VBox(new ProgressIndicator()), "center");
	private final ResizableRelocatableStageDecorator stageDecorator = new ResizableRelocatableStageDecorator(stage);

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
		final Workbench workbench = Workbench.getInstance();
		if (null != workbench) {
			workbench.showGlass(true);
		}
		stage.show();
	}

	public void hide() {
		final Workbench workbench = Workbench.getInstance();
		if (null != workbench) {
			workbench.showGlass(false);
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
