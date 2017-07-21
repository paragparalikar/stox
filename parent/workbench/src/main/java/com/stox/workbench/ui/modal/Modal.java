package com.stox.workbench.ui.modal;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.stox.core.intf.HasLifecycle;
import com.stox.workbench.ui.stage.ResizableRelocatableStageDecorator;
import com.stox.workbench.ui.titlebar.TitleBar;
import com.stox.workbench.ui.titlebar.decorator.CloseDecorator;

public class Modal implements HasLifecycle {

	private final TitleBar titleBar = new TitleBar();
	private final CloseDecorator closeDecorator = new CloseDecorator();
	private final VBox container = new VBox();
	private final BorderPane root = new BorderPane(container, titleBar.getNode(), null, null, null);
	private final Stage stage = new Stage();
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

	public void setContent(final Node node) {
		container.getChildren().clear();
		VBox.setVgrow(node, Priority.ALWAYS);
		node.getStyleClass().add("content");
		container.getChildren().add(node);
	}

	public void setButtonGroup(final Node node) {
		root.setBottom(node);
	}

	public List<String> getStyleClass() {
		return root.getStyleClass();
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {
		stage.hide();
	}

	public void show() {
		stage.show();
	}

	public void hide() {
		stage.hide();
	}

	public void addStylesheet(final String path) {
		stage.getScene().getStylesheets().add(path);
	}

	public void setTitle(final String title) {
		titleBar.setTitleText(title);
	}

	protected TitleBar getTitleBar() {
		return titleBar;
	}

}
