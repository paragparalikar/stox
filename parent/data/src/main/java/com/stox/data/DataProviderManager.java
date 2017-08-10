package com.stox.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.intf.Callback;
import com.stox.core.intf.HasName.HasNameComaparator;
import com.stox.data.event.DataProviderChangedEvent;
import com.stox.data.ui.DataProviderSelectionModal;

@Component
public class DataProviderManager {

	private volatile boolean selectionInProgress = false;
	private List<DataProvider> dataProviders;
	private DataProvider selectedDataProvider;
	private final List<Callback<DataProvider, Void>> callbacks = new LinkedList<>();

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshed(final ContextRefreshedEvent event) {
		selectedDataProvider = null;
		dataProviders = new ArrayList<>(event.getApplicationContext().getBeansOfType(DataProvider.class).values());
		dataProviders.sort(new HasNameComaparator<>());
	}

	public void execute(final Callback<DataProvider, Void> callback) {
		if (selectionInProgress) {
			callbacks.add(callback);
			return;
		}
		if (null == selectedDataProvider) {
			selectionInProgress = true;
			callbacks.add(callback);
			Platform.runLater(() -> {
				final DataProviderSelectionModal modal = new DataProviderSelectionModal(dataProviders, dataProvider -> {
					selectedDataProvider = dataProvider;
					eventPublisher.publishEvent(new DataProviderChangedEvent(DataProviderManager.this, dataProvider));
					selectionInProgress = false;
					callbacks.forEach(c -> {
						try {
							c.call(dataProvider);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							callbacks.remove(c);
						}
					});
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
