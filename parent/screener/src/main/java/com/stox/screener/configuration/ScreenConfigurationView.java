package com.stox.screener.configuration;

import java.util.ArrayList;
import java.util.List;

import com.stox.core.ui.auto.AutoView;
import com.stox.core.ui.util.UiUtil;
import com.stox.screener.ScreenConfiguration;
import com.stox.screener.ScreenerViewState;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ScreenConfigurationView extends ScrollPane{
	
	private final VBox container = new VBox();
	private final List<ConfigurationView> configurationViews = new ArrayList<>();
	
	public ScreenConfigurationView(final ScreenerViewState screenerViewState) {
		container.setSpacing(30);
		setContent(container);
		screenerViewState.getScreenConfigurations().forEach(config -> {
			final ConfigurationView configurationView = new ConfigurationView(config);
			configurationViews.add(configurationView);
			container.getChildren().add(configurationView);
		});
	}
	
	public void updateModel() {
		configurationViews.forEach(ConfigurationView::updateModel);
	}

}

class ConfigurationView extends VBox{
	
	private final AutoView autoView;
	private final ScreenConfiguration screenConfiguration;
	private final Label titleLabel = UiUtil.classes(new Label(), "h2");
	private final Label spanLabel = new Label("Matching Span");
	private final TextField spanTextField = new TextField();
	private final HBox spanContainer = new HBox(spanLabel, UiUtil.spacer(), spanTextField);
	private final Label offsetLabel = new Label("Offset");
	private final TextField offsetTextField = new TextField();
	private final HBox offsetContainer = new HBox(offsetLabel, UiUtil.spacer(), offsetTextField);
	
	public ConfigurationView(final ScreenConfiguration screenConfiguration) {
		this.screenConfiguration = screenConfiguration;
		titleLabel.setText(screenConfiguration.getScreen().getName());
		autoView = new AutoView(screenConfiguration.getConfiguration());
		autoView.getChildren().add(0, offsetContainer);
		autoView.getChildren().add(0, spanContainer);
		getChildren().addAll(titleLabel, autoView);
		
		spanTextField.setText(String.valueOf(screenConfiguration.getSpan()));
		offsetTextField.setText(String.valueOf(screenConfiguration.getOffset()));
	}
	
	public void updateModel() {
		screenConfiguration.setSpan(Integer.parseInt(spanTextField.getText()));
		screenConfiguration.setOffset(Integer.parseInt(offsetTextField.getText()));
		autoView.updateModel();
	}
	
}