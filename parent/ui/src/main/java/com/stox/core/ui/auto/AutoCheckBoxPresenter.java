package com.stox.core.ui.auto;

import java.lang.reflect.Field;

import com.stox.core.ui.Container;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.StringUtil;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class AutoCheckBoxPresenter implements AutoPresenter {

	private final Field field;
	private final Object model;
	private final Label label = new Label();
	private final CheckBox node = new CheckBox();
	private final HBox widgetContainer = new HBox(node);
	private final HBox hBox = new HBox(label, UiUtil.spacer(), widgetContainer);
	
	public AutoCheckBoxPresenter(final Field field, final Object model) {
		this.field = field;
		this.model = model;
		widgetContainer.setPrefWidth(100);
		label.setLabelFor(node);
		label.setText(StringUtil.splitCamelCase(field.getName()));
	}

	@Override
	public void present(Container container) {
		updateView();
		container.add(hBox);
	}


	@Override
	public void updateModel() {
		try {
			field.set(model, node.isSelected());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateView() {
		try {
			final Boolean value = (Boolean) field.get(model);
			node.setSelected(null == value ? false : value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
