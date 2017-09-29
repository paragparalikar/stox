package com.stox.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasName.HasNameComaparator;
import com.stox.data.event.DataProviderChangedEvent;
import com.stox.data.ui.DataProviderSelectionModal;

import javafx.application.Platform;

@Component
public class DataProviderManager {

	private volatile boolean selectionInProgress = false;
	private List<DataProvider> dataProviders;
	private DataProvider selectedDataProvider;
	private final List<Consumer<DataProvider>> callbacks = new LinkedList<>();

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	public DataProvider getSelectedDataProvider() {
		return selectedDataProvider;
	}

	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshed(final ContextRefreshedEvent event) {
		selectedDataProvider = null;
		dataProviders = new ArrayList<>(event.getApplicationContext().getBeansOfType(DataProvider.class).values());
		dataProviders.sort(new HasNameComaparator<>());
	}

	public void execute(final Consumer<DataProvider> callback) {
		if (selectionInProgress) {
			callbacks.add(callback);
			return;
		}
		if (null == selectedDataProvider) {
			selectionInProgress = true;
			callbacks.add(callback);
			Platform.runLater(() -> {
				final DataProviderSelectionModal modal = new DataProviderSelectionModal(dataProviders, dataProvider -> {
					taskExecutor.execute(() -> {
						if(dataProvider.isLoggedIn()) {
							select(dataProvider);
						}else {
							try {
								dataProvider.login(() -> {
									select(dataProvider);
								});
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
					});
				});
				modal.getStyleClass().add("primary");
				modal.show();
			});
		} else {
			callback.accept(selectedDataProvider);
		}
	}
	
	protected void select(final DataProvider dataProvider) {
		eventPublisher.publishEvent(new DataProviderChangedEvent(DataProviderManager.this, selectedDataProvider, dataProvider));
		selectedDataProvider = dataProvider;
		selectionInProgress = false;
		callbacks.removeIf(callback -> {
			try {
				callback.accept(dataProvider);
			} catch (Exception e) {
				e.printStackTrace();
			} 
			return true;
		});
	}

}
