package com.stox.example.ui;

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
import com.stox.example.client.ExampleClient;
import com.stox.example.client.ExampleGroupClient;
import com.stox.example.event.ExampleCreatedEvent;
import com.stox.example.event.ExampleDeletedEvent;
import com.stox.example.event.ExampleGroupCreatedEvent;
import com.stox.example.event.ExampleGroupDeletedEvent;
import com.stox.example.event.ExampleGroupEditedEvent;
import com.stox.example.model.Example;
import com.stox.example.model.ExampleGroup;
import com.stox.example.model.ExampleViewState;
import com.stox.workbench.ui.view.Link.State;
import com.stox.workbench.ui.view.PublisherPresenter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

@Component
@Scope("prototype")
public class ExamplePresenter extends PublisherPresenter<ExampleView, ExampleViewState> {

	@Autowired
	private ExampleGroupClient exampleGroupClient;

	@Autowired
	private ExampleClient exampleClient;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	private final ExampleView view = new ExampleView();

	public ExamplePresenter() {
		final ComboBox<ExampleGroup> exampleGroupComboBox = view.getExampleGroupComboBox();
		exampleGroupComboBox.setConverter(new ExampleGroupStringConverter());
		bind();
	}

	@PostConstruct
	public void postConstruct() {
		exampleGroupClient.loadAll(new ResponseCallback<List<ExampleGroup>>() {
			@Override
			public void onSuccess(final Response<List<ExampleGroup>> response) {
				final List<ExampleGroup> exampleGroups = response.getPayload();
				if (null != exampleGroups && !exampleGroups.isEmpty()) {
					view.getExampleGroupComboBox().getItems().addAll(exampleGroups);
					// TODO default selection should be handled after the state is set
					view.getExampleGroupComboBox().getSelectionModel().select(0);
				}
			}
		});
	}

	private void bind() {
		view.getSearchButton().selectedProperty().addListener((observable, oldValue, value) -> {
			showSearchBox(value);
		});
		view.getAddButton().addEventHandler(ActionEvent.ACTION, event -> {
			createExampleGroup();
		});
		view.getEditButton().addEventHandler(ActionEvent.ACTION, event -> {
			editExampleGroup(view.getExampleGroupComboBox().getValue());
		});
		view.getDeleteButton().addEventHandler(ActionEvent.ACTION, event -> {
			deleteExampleGroup(view.getExampleGroupComboBox().getValue());
		});
		view.getExampleGroupComboBox().getSelectionModel().selectedItemProperty()
				.addListener((observable, old, exampleGroup) -> {
					selectExampleGroup(exampleGroup);
				});
		view.getExampleTableView().getSelectionModel().selectedItemProperty().addListener((observable, old, example) -> {
			selectExample(example);
		});
		view.setDeleteConsumer(example -> {
			deleteExample(example);
		});
	}

	private void showSearchBox(final boolean value) {
		if (value) {
			view.getTitleBar().add(Side.BOTTOM, 0, view.getSearchTextField());
		} else {
			view.getTitleBar().remove(Side.BOTTOM, view.getSearchTextField());
		}
	}

	private void createExampleGroup() {
		final ExampleGroupEditorModal modal = new ExampleGroupEditorModal(null, exampleGroupClient);
		modal.show();
	}

	private void editExampleGroup(final ExampleGroup exampleGroup) {
		if (null != exampleGroup) {
			final ExampleGroupEditorModal modal = new ExampleGroupEditorModal(exampleGroup, exampleGroupClient);
			modal.show();
		}
	}

	private void deleteExampleGroup(final ExampleGroup exampleGroup) {
		if (null != exampleGroup && null != exampleGroup.getId()) {
			final Confirmation confirmation = new Confirmation("Delete ExampleGroup",
					"Are you sure you want to delete \"" + exampleGroup.getName() + "\"?");
			confirmation.getStyleClass().add("danger");
			confirmation.getActionButton().getStyleClass().add("danger");
			confirmation.show();
			confirmation.getActionButton().addEventHandler(ActionEvent.ACTION, e -> {
				exampleGroupClient.delete(exampleGroup.getId(), new ResponseCallback<ExampleGroup>() {
					@Override
					public void onSuccess(Response<ExampleGroup> response) {
						confirmation.hide();
						if (!view.getExampleGroupComboBox().getItems().isEmpty()) {
							view.getExampleGroupComboBox().getSelectionModel().select(0);
						}
					}

					@Override
					public void onFailure(Response<ExampleGroup> response, Throwable throwable) {
						final String message = null == throwable || null == throwable.getMessage()
								? "Failed to delete example group"
								: throwable.getMessage();
						view.setMessage(new Message(message, MessageType.ERROR));
					}
				});
			});
		}
	}

	private void selectExampleGroup(final ExampleGroup exampleGroup) {
		view.getExampleTableView().getItems().clear();
		if (null != exampleGroup && null != exampleGroup.getId()) {
			exampleClient.load(exampleGroup.getId(), new ResponseCallback<List<Example>>() {
				@Override
				public void onSuccess(Response<List<Example>> response) {
					view.getExampleTableView().getItems().setAll(response.getPayload());
				}

				@Override
				public void onFailure(Response<List<Example>> response, Throwable throwable) {
					final String message = null == throwable || null == throwable.getMessage()
							? "Failed to load entries"
							: throwable.getMessage();
					view.setMessage(new Message(message, MessageType.ERROR));
				}
			});
		}
	}

	private void selectExample(final Example example) {
		if (null != example) {
			publish(new State(example.getInstrumentId(), example.getBarSpan(), example.getDate().getTime()));
		}
	}

	private void deleteExample(final Example example) {
		if (null != example && StringUtil.hasText(example.getId())) {
			final ExampleGroup exampleGroup = view.getExampleGroupComboBox().getValue();
			if (null != exampleGroup) {
				exampleClient.delete(exampleGroup.getId(), example.getId(), new ResponseCallback<Example>() {
					@Override
					public void onSuccess(Response<Example> response) {
						// No op
					}
				});
			}
		}
	}

	public void onExampleGroupCreated(final ExampleGroupCreatedEvent event) {
		final ExampleGroup exampleGroup = event.getExampleGroup();
		final ObservableList<ExampleGroup> items = view.getExampleGroupComboBox().getItems();
		items.add(exampleGroup);
		FXCollections.sort(items, new HasNameComaparator<>());
		if (1 == items.size()) {
			view.getExampleGroupComboBox().getSelectionModel().select(0);
		}
	}

	public void onExampleGroupDeleted(final ExampleGroupDeletedEvent event) {
		view.getExampleGroupComboBox().getItems()
				.removeIf(exampleGroup -> exampleGroup.getId().equals(event.getExampleGroup().getId()));
	}

	public void onExampleGroupEdited(final ExampleGroupEditedEvent event) {
		final ExampleGroup exampleGroup = event.getExampleGroup();
		final ObservableList<ExampleGroup> exampleGroups = view.getExampleGroupComboBox().getItems();
		exampleGroups.removeIf(w -> w.getId().equals(exampleGroup.getId()));
		exampleGroups.add(exampleGroup);
		FXCollections.sort(exampleGroups, new HasNameComaparator<>());
		view.getExampleGroupComboBox().getSelectionModel().select(exampleGroup);
	}

	public void onExampleCreated(final ExampleCreatedEvent event) {
		final ExampleGroup exampleGroup = view.getExampleGroupComboBox().getValue();
		final Example example = event.getExample();
		if (null != exampleGroup && null != example && null != exampleGroup.getId()
				&& exampleGroup.getId().equals(example.getExampleGroupId())) {
			view.getExampleTableView().getItems().add(example);
			FXCollections.sort(view.getExampleTableView().getItems(), new HasNameComaparator<>());
		}
	}

	public void onExampleDeleted(final ExampleDeletedEvent event) {
		final ExampleGroup exampleGroup = view.getExampleGroupComboBox().getValue();
		final Example example = event.getExample();
		if (null != exampleGroup && null != example && null != exampleGroup.getId()
				&& exampleGroup.getId().equals(example.getExampleGroupId())) {
			view.getExampleTableView().getItems().removeIf(entry -> entry.getId().equals(event.getExample().getId()));
		}
	}

	@Override
	public ExampleView getView() {
		return view;
	}

	@Override
	public ExampleViewState getViewState() {
		final ExampleViewState viewState = new ExampleViewState();
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
