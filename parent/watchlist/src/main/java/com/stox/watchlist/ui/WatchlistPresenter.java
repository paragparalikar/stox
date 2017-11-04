package com.stox.watchlist.ui;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasName.HasNameComaparator;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.model.Response;
import com.stox.core.ui.widget.modal.Confirmation;
import com.stox.core.util.StringUtil;
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
import com.stox.workbench.ui.view.Link.State;
import com.stox.workbench.ui.view.StatePublisherPresenter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

@Component
@Scope("prototype")
public class WatchlistPresenter extends StatePublisherPresenter<WatchlistView, WatchlistViewState> {

	@Autowired
	private WatchlistClient watchlistClient;

	@Autowired
	private WatchlistEntryClient watchlistEntryClient;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	private final WatchlistView view = new WatchlistView();

	public WatchlistPresenter() {
		final ComboBox<Watchlist> watchlistComboBox = view.getWatchlistComboBox();
		watchlistComboBox.setConverter(new WatchlistStringConverter());
		bind();
	}

	@PostConstruct
	public void postConstruct() {
		watchlistClient.loadAll(new ResponseCallback<List<Watchlist>>() {
			@Override
			public void onSuccess(final Response<List<Watchlist>> response) {
				final List<Watchlist> watchlists = response.getPayload();
				if (null != watchlists && !watchlists.isEmpty()) {
					view.getWatchlistComboBox().getItems().addAll(watchlists);
					// TODO default selection should be handled after the state is set
					view.getWatchlistComboBox().getSelectionModel().select(0);
				}
			}
		});
	}

	private void bind() {
		view.getSearchButton().selectedProperty().addListener((observable, oldValue, value) -> {
			showSearchBox(value);
		});
		view.getAddButton().addEventHandler(ActionEvent.ACTION, event -> {
			createWatchlist();
		});
		view.getEditButton().addEventHandler(ActionEvent.ACTION, event -> {
			editWatchlist(view.getWatchlistComboBox().getValue());
		});
		view.getDeleteButton().addEventHandler(ActionEvent.ACTION, event -> {
			deleteWatchlist(view.getWatchlistComboBox().getValue());
		});
		view.getWatchlistComboBox().getSelectionModel().selectedItemProperty()
				.addListener((observable, old, watchlist) -> {
					selectWatchlist(watchlist);
				});
		view.getEntryTableView().getSelectionModel().selectedItemProperty().addListener((observable, old, entry) -> {
			selectWatchlistEntry(entry);
		});
		view.setDeleteConsumer(entry -> {
			deleteWatchlistEntry(entry);
		});
	}

	private void showSearchBox(final boolean value) {
		if (value) {
			view.getTitleBar().add(Side.BOTTOM, 0, view.getSearchTextField());
		} else {
			view.getTitleBar().remove(Side.BOTTOM, view.getSearchTextField());
		}
	}

	private void createWatchlist() {
		final WatchlistEditorModal modal = new WatchlistEditorModal(null, watchlistClient);
		modal.show();
	}

	private void editWatchlist(final Watchlist watchlist) {
		if (null != watchlist) {
			final WatchlistEditorModal modal = new WatchlistEditorModal(watchlist, watchlistClient);
			modal.show();
		}
	}

	private void deleteWatchlist(final Watchlist watchlist) {
		if (null != watchlist && null != watchlist.getId()) {
			final Confirmation confirmation = new Confirmation("Delete Watchlist",
					"Are you sure you want to delete \"" + watchlist.getName() + "\"?");
			confirmation.getStyleClass().add("danger");
			confirmation.getActionButton().getStyleClass().add("danger");
			confirmation.show();
			confirmation.getActionButton().addEventHandler(ActionEvent.ACTION, e -> {
				watchlistClient.delete(watchlist.getId(), new ResponseCallback<Watchlist>() {
					@Override
					public void onSuccess(Response<Watchlist> response) {
						confirmation.hide();
						if (!view.getWatchlistComboBox().getItems().isEmpty()) {
							view.getWatchlistComboBox().getSelectionModel().select(0);
						}
					}

					@Override
					public void onFailure(Response<Watchlist> response, Throwable throwable) {
						final String message = null == throwable || null == throwable.getMessage()
								? "Failed to delete watchlist"
								: throwable.getMessage();
						view.setMessage(new Message(message, MessageType.ERROR));
					}
				});
			});
		}
	}

	private void selectWatchlist(final Watchlist watchlist) {
		view.getEntryTableView().getItems().clear();
		if (null != watchlist && null != watchlist.getId()) {
			watchlistEntryClient.load(watchlist.getId(), new ResponseCallback<List<WatchlistEntry>>() {
				@Override
				public void onSuccess(Response<List<WatchlistEntry>> response) {
					view.getEntryTableView().getItems().setAll(response.getPayload());
				}

				@Override
				public void onFailure(Response<List<WatchlistEntry>> response, Throwable throwable) {
					final String message = null == throwable || null == throwable.getMessage()
							? "Failed to load entries"
							: throwable.getMessage();
					view.setMessage(new Message(message, MessageType.ERROR));
				}
			});
		}
	}

	private void selectWatchlistEntry(final WatchlistEntry entry) {
		if (null != entry) {
			publish(new State(entry.getInstrumentId(), entry.getBarSpan(), 0));
		}
	}

	private void deleteWatchlistEntry(final WatchlistEntry entry) {
		if (null != entry && StringUtil.hasText(entry.getId())) {
			final Watchlist watchlist = view.getWatchlistComboBox().getValue();
			if (null != watchlist) {
				watchlistEntryClient.delete(watchlist.getId(), entry.getId(), new ResponseCallback<WatchlistEntry>() {
					@Override
					public void onSuccess(Response<WatchlistEntry> response) {
						// No op
					}
				});
			}
		}
	}

	public void onWatchlistCreated(final WatchlistCreatedEvent event) {
		Platform.runLater(() -> {
			final Watchlist watchlist = event.getWatchlist();
			final ObservableList<Watchlist> items = view.getWatchlistComboBox().getItems();
			items.add(watchlist);
			FXCollections.sort(items, new HasNameComaparator<>());
			if (1 == items.size()) {
				view.getWatchlistComboBox().getSelectionModel().select(0);
			}
		});
	}

	public void onWatchlistDeleted(final WatchlistDeletedEvent event) {
		Platform.runLater(() -> {
			view.getWatchlistComboBox().getItems()
					.removeIf(watchlist -> watchlist.getId().equals(event.getWatchlist().getId()));
		});
	}

	public void onWatchlistEdited(final WatchlistEditedEvent event) {
		Platform.runLater(() -> {
			final Watchlist watchlist = event.getWatchlist();
			view.getWatchlistComboBox().getItems().removeIf(w -> w.getId().equals(watchlist.getId()));
			view.getWatchlistComboBox().getItems().add(watchlist);
			FXCollections.sort(view.getWatchlistComboBox().getItems(), new HasNameComaparator<>());
			view.getWatchlistComboBox().getSelectionModel().select(watchlist);
		});
	}

	public void onWatchlistEntryCreated(final WatchlistEntryCreatedEvent event) {
		Platform.runLater(() -> {
			final Watchlist watchlist = view.getWatchlistComboBox().getValue();
			final WatchlistEntry watchlistEntry = event.getEntry();
			if (null != watchlist && null != watchlistEntry && null != watchlist.getId()
					&& watchlist.getId().equals(watchlistEntry.getWatchlistId())) {
				view.getEntryTableView().getItems().add(watchlistEntry);
				FXCollections.sort(view.getEntryTableView().getItems(), new HasNameComaparator<>());
			}
		});
	}

	public void onWatchlistEntryDeleted(final WatchlistEntryDeletedEvent event) {
		Platform.runLater(() -> {
			final Watchlist watchlist = view.getWatchlistComboBox().getValue();
			final WatchlistEntry watchlistEntry = event.getEntry();
			if (null != watchlist && null != watchlistEntry && null != watchlist.getId()
					&& watchlist.getId().equals(watchlistEntry.getWatchlistId())) {
				view.getEntryTableView().getItems().removeIf(entry -> entry.getId().equals(event.getEntry().getId()));
			}
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
