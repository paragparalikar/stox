package com.stox.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.intf.Callback;
import com.stox.data.ui.DataProviderSelectionModal;

@Component
public class DataProviderManager {

	private volatile boolean selectionInProgress = false;
	private Collection<DataProvider> dataProviders;
	private DataProvider selectedDataProvider;
	private final List<Callback<DataProvider, Void>> callbacks = new LinkedList<>();

	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshed(final ContextRefreshedEvent event) {
		selectedDataProvider = null;
		dataProviders = event.getApplicationContext().getBeansOfType(DataProvider.class).values();
	}

	public void execute(final Callback<DataProvider, Void> callback) {
		if (selectionInProgress) {
			callbacks.add(callback);
			return;
		}
		if (null == selectedDataProvider) {
			selectionInProgress = true;
			Platform.runLater(() -> {
				final DataProviderSelectionModal modal = new DataProviderSelectionModal(dataProviders, dataProvider -> {
					selectedDataProvider = dataProvider;
					selectionInProgress = false;
					callbacks.forEach(c -> c.call(dataProvider));
					return null;
				});
				modal.getStyleClass().add("primary");
				modal.show();
			});
		} else {
			callback.call(selectedDataProvider);
		}
	}

}
