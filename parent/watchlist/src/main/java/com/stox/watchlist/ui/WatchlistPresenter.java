package com.stox.watchlist.ui;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasName.HasNameComaparator;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Response;
import com.stox.core.ui.widget.modal.Confirmation;
import com.stox.watchlist.client.WatchlistClient;
import com.stox.watchlist.client.WatchlistEntryClient;
import com.stox.watchlist.event.WatchlistCreatedEvent;
import com.stox.watchlist.event.WatchlistDeletedEvent;
import com.stox.watchlist.event.WatchlistEditedEvent;
import com.stox.watchlist.event.WatchlistEntryCreatedEvent;
import com.stox.watchlist.event.WatchlistEntryDeletedEvent;
import com.stox.watchlist.model.Watchlist;
import com.stox.watchlist.model.WatchlistEntry;
import com.stox.watchlist.model.WatchlistViewState;
import com.stox.workbench.ui.view.PublisherPresenter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.layout.Pane;

@Component
@Scope("prototype")
public class WatchlistPresenter extends PublisherPresenter<WatchlistView, WatchlistViewState> {

	@Autowired
	private WatchlistClient watchlistClient;

	@Autowired
	private WatchlistEntryClient watchlistEntryClient;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	private final WatchlistView view = new WatchlistView();

	public WatchlistPresenter() {
		view.getSearchButton().selectedProperty().addListener((observable, oldValue, value) -> {
			if (value) {
				view.getTitleBar().add(Side.BOTTOM, 0, view.getSearchTextField());
			} else {
				view.getTitleBar().remove(Side.BOTTOM, view.getSearchTextField());
			}
		});
		view.getAddButton().addEventHandler(ActionEvent.ACTION, event -> {
			final WatchlistEditorModal modal = new WatchlistEditorModal(null, watchlistClient);
			modal.show();
		});
		view.getEditButton().addEventHandler(ActionEvent.ACTION, event -> {
			final Watchlist watchlist = view.getWatchlistComboBox().getValue();
			if (null != watchlist) {
				final WatchlistEditorModal modal = new WatchlistEditorModal(watchlist, watchlistClient);
				modal.show();
			}
		});
		view.getDeleteButton().addEventHandler(ActionEvent.ACTION, event -> {
			final Watchlist watchlist = view.getWatchlistComboBox().getValue();
			if (null != watchlist) {
				final Confirmation confirmation = new Confirmation("Delete Watchlist",
						"Are you sure you want to delete \"" + watchlist.getName() + "\"?");
				confirmation.show();
				confirmation.getActionButton().addEventHandler(ActionEvent.ACTION, e -> {
					watchlistClient.delete(watchlist.getId(), new ResponseCallback<Watchlist>() {
						@Override
						public void onSuccess(Response<Watchlist> response) {
							confirmation.hide();
						}
					});
				});
			}
		});
		view.setDeleteConsumer(entry -> {
			final Watchlist watchlist = view.getWatchlistComboBox().getValue();
			if (null != watchlist) {
				watchlistEntryClient.delete(watchlist.getId(), entry.getId(), new ResponseCallback<WatchlistEntry>() {
					@Override
					public void onSuccess(Response<WatchlistEntry> response) {
						// No op
					}
				});
			}
		});
	}

	@PostConstruct
	public void postConstruct() {
		watchlistClient.loadAll(new ResponseCallback<List<Watchlist>>() {
			@Override
			public void onSuccess(final Response<List<Watchlist>> response) {
				Platform.runLater(() -> {
					view.getWatchlistComboBox().getItems().setAll(response.getPayload());
				});
			}
		});
	}

	@EventListener
	public void onWatchlistCreated(final WatchlistCreatedEvent event) {
		Platform.runLater(() -> {
			view.getWatchlistComboBox().getItems().add(event.getWatchlist());
			FXCollections.sort(view.getWatchlistComboBox().getItems(), new HasNameComaparator<>());
		});
	}

	@EventListener
	public void onWatchlistDeleted(final WatchlistDeletedEvent event) {
		Platform.runLater(() -> {
			view.getWatchlistComboBox().getItems()
					.removeIf(watchlist -> watchlist.getId().equals(event.getWatchlist().getId()));
		});
	}

	@EventListener
	public void onWatchlistEdited(final WatchlistEditedEvent event) {
		Platform.runLater(() -> {
			view.getWatchlistComboBox().getItems()
					.removeIf(watchlist -> watchlist.getId().equals(event.getWatchlist().getId()));
			view.getWatchlistComboBox().getItems().add(event.getWatchlist());
			FXCollections.sort(view.getWatchlistComboBox().getItems(), new HasNameComaparator<>());
		});
	}

	@EventListener
	public void onWatchlistEntryCreated(final WatchlistEntryCreatedEvent event) {
		Platform.runLater(() -> {
			view.getEntryTableView().getItems().add(event.getEntry());
			FXCollections.sort(view.getEntryTableView().getItems(), new HasNameComaparator<>());
		});
	}
	
	@EventListener
	public void onWatchlistEntryDeleted(final WatchlistEntryDeletedEvent event) {
		Platform.runLater(() -> {
			view.getEntryTableView().getItems().removeIf(entry -> entry.getId().equals(event.getEntry().getId()));
		});
	}

	@Override
	public WatchlistView getView() {
		return view;
	}

	@Override
	public WatchlistViewState getViewState() {
		final WatchlistViewState viewState = new WatchlistViewState();
		populateViewState(viewState);
		return viewState;
	}

	@Override
	public void setDefaultPosition() {
		if (null != view.getParent()) {
			final Pane pane = (Pane) view.getParent();
			setPosition(0, 0, pane.getWidth() / 6, pane.getHeight());
		}
	}

	@Override
	public void publish(ApplicationEvent event) {
		eventPublisher.publishEvent(event);
	}

}
