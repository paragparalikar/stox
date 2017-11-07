package com.stox.core.ui.auto;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.stox.core.ui.Container;
import com.stox.core.ui.util.UiUtil;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class AutoView<T> extends VBox implements Container{

	private final List<AutoPresenter> presenters = new LinkedList<>();

	public AutoView(final T model) {
		build(model);
		UiUtil.classes(this, "container");
	}

	private void build(T model) {
		final AutoPresenterFactory autoPresenterFactory = new AutoPresenterFactory();
		final Field[] fields = model.getClass().getDeclaredFields();
		Arrays.stream(fields)
				.filter(field -> !Modifier.isTransient(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
				.forEach(field -> {
					final AutoPresenter autoPresenter = autoPresenterFactory.get(field, model);
					presenters.add(autoPresenter);
					autoPresenter.present(this);
				});
	}

	public void updateModel() {
		presenters.forEach(AutoPresenter::updateModel);
	}

	@Override
	public void showSpinner(boolean value) {
		
	}

	@Override
	public boolean contains(Node content) {
		return getChildren().contains(content);
	}

	@Override
	public void add(Node content) {
		getChildren().add(content);
	}

	@Override
	public void remove(Node content) {
		getChildren().remove(content);
	}

}