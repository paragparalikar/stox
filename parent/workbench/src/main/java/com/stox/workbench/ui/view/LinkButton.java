package com.stox.workbench.ui.view;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import com.stox.core.intf.Persistable;
import com.stox.core.ui.util.UiUtil;

public class LinkButton extends Button implements Persistable {

	private final ObjectProperty<Link> linkProperty = new SimpleObjectProperty<Link>(Link.BROWN);

	public LinkButton() {
		getStyleClass().addAll("primary", "scrip-link-button");
		setGraphicFor(linkProperty.get());

		linkProperty.addListener((observable, oldValue, newValue) -> setGraphicFor(newValue));
		addEventHandler(ActionEvent.ACTION, event -> showContextMenu());
	}

	private void showContextMenu() {
		final ContextMenu menu = new ContextMenu();
		menu.getStyleClass().add("scrip-link-context-menu");
		for (final Link link : Link.values()) {
			final MenuItem item = new MenuItem("", createGraphic(link.getColor()));
			item.getStyleClass().add("scrip-link-menu-item");
			item.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					linkProperty().set(link);
					menu.hide();
				}
			});
			menu.getItems().add(item);
		}
		final Point2D point = localToScreen(0, getHeight());
		menu.show(this, point.getX(), point.getY());
	}

	public ObjectProperty<Link> linkProperty() {
		return linkProperty;
	}

	private void setGraphicFor(final Link link) {
		setGraphic(createGraphic(link.getColor()));
	}

	private Node createGraphic(final Color color) {
		final Circle circle = new Circle(7.5);
		circle.setStyle("link-color:" + UiUtil.web(color) + ";");
		return circle;
	}

	public void setLink(final Link link) {
		linkProperty.set(link);
	}

	public Link getLink() {
		return linkProperty.get();
	}

	@Override
	public void read(DataInput input) throws IOException {
		final int ordinal = input.readInt();
		if (0 <= ordinal) {
			setLink(Link.values()[ordinal]);
		}
	}

	@Override
	public void write(DataOutput output) throws IOException {
		final Link scripLink = getLink();
		output.writeInt(null == scripLink ? -1 : scripLink.ordinal());
	}
}
