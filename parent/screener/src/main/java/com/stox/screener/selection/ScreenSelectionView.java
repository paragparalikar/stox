package com.stox.screener.selection;

import java.util.List;

import com.stox.screen.Screen;
import com.stox.screener.ScreenConfiguration;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import lombok.Getter;

@Getter
@SuppressWarnings("rawtypes")
public class ScreenSelectionView extends ListView<Screen> {

	private final List<ScreenConfiguration> screenConfigurations;

	public ScreenSelectionView(final List<ScreenConfiguration> screenConfigurations) {
		this.screenConfigurations = screenConfigurations;
		setCellFactory(param -> new ScreenSelectionCell(screenConfigurations));
	}

}

@SuppressWarnings("rawtypes")
class ScreenSelectionCell extends ListCell<Screen> {

	private ScreenConfiguration screenConfiguration;
	private final CheckBox checkBox = new CheckBox();

	public ScreenSelectionCell(final List<ScreenConfiguration> screenConfigurations) {
		checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue) {
				if(null == screenConfiguration) {
					screenConfiguration = ScreenConfiguration.builder().screen(getItem())
							.configuration(getItem().buildDefaultConfig()).build();
				}
				screenConfigurations.add(screenConfiguration);
			}else {
				screenConfigurations.remove(screenConfiguration);
			}
		});
	}

	@Override
	protected void updateItem(Screen item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty && null != item) {
			setGraphic(checkBox);
			setText(item.getName());
		} else {
			setGraphic(null);
			setText(null);
		}
	}

}
