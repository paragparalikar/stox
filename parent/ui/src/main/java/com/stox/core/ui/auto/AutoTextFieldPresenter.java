package com.stox.core.ui.auto;

import java.lang.reflect.Field;

import com.stox.core.ui.Container;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.StringUtil;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class AutoTextFieldPresenter implements AutoPresenter {
	
	private final Field field;
	private final Object model;
	private final Label label = new Label();
	private final TextField node = new TextField();
	private final HBox hBox = new HBox(label, UiUtil.spacer(), node);
	
	public AutoTextFieldPresenter(final Field field, final Object model) {
		this.field = field;
		this.model = model;
		node.setPrefWidth(100);
		label.setLabelFor(node);
		final String name = StringUtil.splitCamelCase(field.getName());
		label.setText(name);
		node.setPromptText(name);
	}

	@Override
	public void present(Container container) {
		updateView();
		container.add(hBox);
	}

	@Override
	public void updateModel() {
		try {
			final String text = node.getText();
			final Class<?> type = field.getType();
			if(int.class.equals(type) || Integer.class.equals(type)) {
				field.set(model, StringUtil.hasText(text) ? Integer.parseInt(text) : 0);
			}else if(double.class.equals(type) || Double.class.equals(type)) {
				field.set(model, StringUtil.hasText(text) ? Double.parseDouble(text) : 0);
			}else if(long.class.equals(type) || Long.class.equals(type)) {
				field.set(model, StringUtil.hasText(text) ? Long.parseLong(text) : 0);
			}else if(short.class.equals(type) || Short.class.equals(type)) {
				field.set(model, StringUtil.hasText(text) ? Short.parseShort(text) : 0);
			}else if(String.class.equals(type)) {
				field.set(model, text );
			}
		} catch (Exception e) {
			//setMessage(new Message(e.getMessage(), MessageType.ERROR));
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateView() {
		try {
			final Object value = field.get(model);
			node.setText(null == value ? "" : String.valueOf(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
