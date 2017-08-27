package com.stox.core.ui.util;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.sun.javafx.scene.control.skin.ListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

public class UiUtil {

	public static String web(final Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
	}

	public static Node graphic(final String content) {
		final Label label = new Label(content);
		label.getStyleClass().add("icon");
		return label;
	}

	public static Node spacer() {
		final Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);
		return region;
	}

	public static void select(final ToggleGroup toggleGroup, final Object value) {
		if (null != toggleGroup) {
			for (final Toggle toggle : toggleGroup.getToggles()) {
				toggle.setSelected(Objects.equals(toggle.getUserData(), value));
			}
		}
	}

	public static void scrollTo(final ListView<?> listView, final int index) {
		final ListViewSkin<?> listViewSkin = (ListViewSkin<?>) listView.getSkin();
		final ObservableList<Node> children = listViewSkin.getChildren();
		if (null != children && !children.isEmpty()) {
			final VirtualFlow<?> virtualFlow = (VirtualFlow<?>) children.get(0);
			final IndexedCell<?> firstCell = virtualFlow.getFirstVisibleCell();
			final IndexedCell<?> lastCell = virtualFlow.getLastVisibleCell();
			if (null != firstCell && null != lastCell) {
				final int firstIndex = firstCell.getIndex();
				final int lastIndex = lastCell.getIndex();
				if (0 <= index && (index < firstIndex || index > lastIndex)) {
					listView.scrollTo(index);
				}
			}
		}
	}

	public static void scrollTo(final TableView<?> tableView, final int index) {
		tableView.scrollTo(index);
		/*
		 * final TableViewSkin<?> tableViewSkin = (TableViewSkin<?>) tableView.getSkin(); final ObservableList<Node> children = tableViewSkin.getChildren(); if (null != children &&
		 * !children.isEmpty()) { final VirtualFlow<?> virtualFlow = (VirtualFlow<?>) children.get(0); final IndexedCell<?> firstCell = virtualFlow.getFirstVisibleCell(); final
		 * IndexedCell<?> lastCell = virtualFlow.getLastVisibleCell(); if (null != firstCell && null != lastCell) { final int firstIndex = firstCell.getIndex(); final int lastIndex
		 * = lastCell.getIndex(); if (0 <= index && (index < firstIndex || index > lastIndex)) { tableView.scrollTo(index); } } }
		 */
	}

	public static HBox radios(final Object[] array, final Object selected, final ToggleGroup toggleGroup) {
		final HBox container = classes(new HBox(), "radio-group");
		for (final Object element : array) {
			if (null != element) {
				final RadioButton radioButton = new RadioButton(element.toString());
				radioButton.setUserData(element);
				radioButton.setToggleGroup(toggleGroup);
				container.getChildren().add(radioButton);
				radioButton.setSelected(element.equals(selected));
			}
		}
		if (null == selected) {
			toggleGroup.getToggles().get(0).setSelected(true);
		}
		return container;
	}

	public static HBox toggles(final Object[] array, final Object selected, final ToggleGroup toggleGroup, final String... classes) {
		final HBox container = new HBox();
		for (final Object element : array) {
			if (null != element) {
				final ToggleButton toggleButton = new ToggleButton(element.toString());
				toggleButton.setUserData(element);
				toggleButton.setToggleGroup(toggleGroup);
				container.getChildren().add(toggleButton);
				toggleButton.setSelected(element.equals(selected));
			}
		}
		box(container);
		childrenClasses(container, classes);
		toggleGroup.getToggles().get(0).setSelected(true);
		return container;
	}

	public static <T extends Parent> T box(final T parent) {
		box(parent.getChildrenUnmodifiable());
		return parent;
	}

	public static void box(final List<? extends Node> children) {
		for (final Node node : children) {
			node.getStyleClass().removeAll("first", "last");
		}

		if (1 < children.size()) {
			children.get(0).getStyleClass().add("first");
			for (int index = 1; index < children.size() - 1; index++) {
				final Node node = children.get(index);
				node.getStyleClass().add("middle");
			}
			children.get(children.size() - 1).getStyleClass().add("last");
		}
	}

	public static <T extends Node> T classes(final T node, String... classes) {
		if(null != node && null != classes) {
			node.getStyleClass().addAll(classes);
		}
		return node;
	}

	public static <T extends Parent> T childrenClasses(final T parent, String... classes) {
		if (null != parent && null != classes) {
			for (final Node node : parent.getChildrenUnmodifiable()) {
				node.getStyleClass().addAll(classes);
			}
		}
		return parent;
	}

	public static <T extends Node> T fullWidth(final T node) {
		HBox.setHgrow(node, Priority.ALWAYS);
		if (node instanceof Region) {
			((Region) node).setMaxWidth(Double.MAX_VALUE);
		}
		return node;
	}

	public static <T extends Node> T fullHeight(final T node) {
		VBox.setVgrow(node, Priority.ALWAYS);
		if (node instanceof Region) {
			((Region) node).setMaxHeight(Double.MAX_VALUE);
		}
		return node;
	}

	public static <T extends Node> T fullArea(final T node) {
		fullWidth(node);
		fullHeight(node);
		return node;
	}

	public static <T extends Control> T tooltip(final T control, final String text) {
		control.setTooltip(new Tooltip(text));
		return control;
	}
}
