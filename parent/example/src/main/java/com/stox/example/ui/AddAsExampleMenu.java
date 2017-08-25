package com.stox.example.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasBarSpan;
import com.stox.core.intf.HasDate;
import com.stox.core.intf.HasInstrument;
import com.stox.core.intf.HasName.HasNameComaparator;
import com.stox.core.intf.ResponseCallback;
import com.stox.core.model.BarSpan;
import com.stox.core.model.Instrument;
import com.stox.core.model.Response;
import com.stox.core.ui.widget.modal.Notification;
import com.stox.example.client.ExampleClient;
import com.stox.example.client.ExampleGroupClient;
import com.stox.example.event.ExampleGroupCreatedEvent;
import com.stox.example.event.ExampleGroupDeletedEvent;
import com.stox.example.event.ExampleGroupEditedEvent;
import com.stox.example.model.Example;
import com.stox.example.model.ExampleGroup;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@Component
@Scope("prototype")
public class AddAsExampleMenu<T extends HasInstrument & HasBarSpan & HasDate> extends Menu {

	private T exampleProvider;

	@Autowired
	private ExampleGroupClient exampleGroupClient;

	@Autowired
	private ExampleClient exampleClient;

	public AddAsExampleMenu() {
		super("Add As Example");
	}

	public void setExampleProvider(T exampleProvider) {
		this.exampleProvider = exampleProvider;
	}

	@PostConstruct
	public void postConstruct() {
		exampleGroupClient.loadAll(new ResponseCallback<List<ExampleGroup>>() {
			@Override
			public void onSuccess(final Response<List<ExampleGroup>> response) {
				final List<MenuItem> items = response.getPayload().stream().sorted(new HasNameComaparator<>())
						.map(exampleGroup -> {
							final MenuItem item = new MenuItem(exampleGroup.getName());
							item.setUserData(exampleGroup);
							item.addEventHandler(ActionEvent.ACTION, event -> addAsExample(exampleGroup));
							return item;
						}).collect(Collectors.toList());
				getItems().setAll(items);
			}
		});
	}

	private void addAsExample(final ExampleGroup exampleGroup) {
		final Instrument instrument = exampleProvider.getInstrument();
		final BarSpan barSpan = exampleProvider.getBarSpan();
		if (null != instrument && null != barSpan) {
			final Example example = new Example();
			example.setExampleGroupId(exampleGroup.getId());
			example.setInstrument(instrument);
			example.setInstrumentId(instrument.getId());
			example.setBarSpan(barSpan);
			example.setDate(exampleProvider.getDate());
			exampleClient.save(example, new ResponseCallback<Example>() {
				@Override
				public void onSuccess(Response<Example> response) {
					final StringBuilder stringBuilder = new StringBuilder("Example created with below details:");
					stringBuilder.append("\nInstrument\t" + instrument.getName());
					stringBuilder.append("\nTimeframe\t" + barSpan.getName());
					stringBuilder.append("\nExampleGroup\t\t" + exampleGroup.getName());
					Notification.builder().style("success").graphic(new Label(stringBuilder.toString())).build().show();
				}

				@Override
				public void onFailure(Response<Example> response, Throwable throwable) {
					Notification.builder().style("danger").graphic(new Label(throwable.getMessage())).build().show();
				}
			});
		}
	}

	public void onExampleGroupCreated(final ExampleGroupCreatedEvent event) {
		final ExampleGroup exampleGroup = event.getExampleGroup();
		final MenuItem item = new MenuItem(exampleGroup.getName());
		item.setUserData(exampleGroup);
		item.addEventHandler(ActionEvent.ACTION, e -> addAsExample(exampleGroup));
		getItems().add(item);
		FXCollections.sort(getItems(), new Comparator<MenuItem>() {
			@Override
			public int compare(MenuItem o1, MenuItem o2) {
				return Objects.compare((ExampleGroup) o1.getUserData(), (ExampleGroup) o2.getUserData(),
						new HasNameComaparator<>());
			}
		});
	}

	public void onExampleGroupDeleted(final ExampleGroupDeletedEvent event) {
		getItems().removeIf(item -> ((ExampleGroup) item.getUserData()).getId().equals(event.getExampleGroup().getId()));
	}

	public void onExampleGroupEdited(final ExampleGroupEditedEvent event) {
		for (final MenuItem item : getItems()) {
			final ExampleGroup exampleGroup = (ExampleGroup) item.getUserData();
			if (exampleGroup.getId().equals(event.getExampleGroup().getId())) {
				Platform.runLater(() -> {
					item.setText(exampleGroup.getName());
					item.setUserData(exampleGroup);
				});
				break;
			}
		}
	}
}
