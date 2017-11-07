package com.stox.workbench.ui.view;

import com.stox.core.model.Message;
import com.stox.core.ui.Container;
import com.stox.core.ui.HasSpinner;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.MessagePane;
import com.stox.core.ui.widget.titlebar.TitleBar;
import com.stox.core.util.StringUtil;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public abstract class View extends StackPane implements HasSpinner, Container{

	public static final int NONE = 0;
	public static final int MOVE = 1;
	public static final int RESIZE = 2;
	public static final double BORDER = 4;

	private int mode = NONE;
	private final String code;
	private final TitleBar titleBar = new TitleBar();
	private final VBox spinner = UiUtil.classes(new VBox(new ProgressIndicator()), "center");
	private final VBox top = new VBox(titleBar.getNode());
	private final BorderPane container = UiUtil.classes(new BorderPane(null, UiUtil.classes(top, "primary"), null, null, null), "primary", "view");

	public View(final String code, final String name, final String icon) {
		this.code = code;
		titleBar.setGraphic(icon);
		titleBar.setTitleText(name);
		getChildren().add(container);
	}

	public void setMessage(final Message message) {
		clearMessages();
		addMessage(message);
	}

	public void addMessage(final Message message) {
		if(Platform.isFxApplicationThread()) {
			doAddMessage(message);
		}else {
			Platform.runLater(() -> {
				doAddMessage(message);
			});
		}
	}
	
	private void doAddMessage(final Message message) {
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
	public boolean contains(Node content) {
		return container.getCenter() == content;
	}
	
	@Override
	public void remove(Node content) {
		if(contains(content)) {
			container.setCenter(null);
		}
	}
	
	@Override
	public void add(final Node node) {
		container.setCenter(node);
	}

	public void setButtonBar(final Node node) {
		container.setBottom(node);
	}
		
	@Override
	public void showSpinner(final boolean value) {
		if(Platform.isFxApplicationThread()) {
			doShowSpinner(value);
		}else {
			Platform.runLater(() -> doShowSpinner(value));
		}
	}
	
	private void doShowSpinner(final boolean value) {
		container.setDisable(value);
		if (value && !getChildren().contains(spinner)) {
			getChildren().add(spinner);
		} else if (!value) {
			getChildren().remove(spinner);
		}
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public String getCode() {
		return code;
	}

	public TitleBar getTitleBar() {
		return titleBar;
	}

}
