package com.stox.core.ui.auto;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.stox.core.intf.HasName;
import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.FormGroup;
import com.stox.core.util.StringUtil;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class AutoView<T> extends VBox {

	private final List<Runnable> widgets = new LinkedList<>();

	public AutoView(final T model) {
		build(model);
	}

	private void build(T model) {
		final Field[] fields = model.getClass().getDeclaredFields();
		Arrays.stream(fields)
				.filter(field -> !Modifier.isTransient(field.getModifiers()) && !Modifier.isFinal(field.getModifiers()))
				.forEach(field -> {
					field.setAccessible(true);
					final Class<?> type = field.getType();
					if (type.equals(boolean.class) || type.equals(Boolean.class)) {
						final AutoCheckBox autoCheckBox = new AutoCheckBox(model, field);
						widgets.add(autoCheckBox);
						getChildren().add(autoCheckBox);
					}else if(type.isEnum()) { 
						final AutoChoiceBox autoChoiceBox = new AutoChoiceBox(model, field);
						widgets.add(autoChoiceBox);
						getChildren().add(autoChoiceBox);
					}else {
						final AutoTextField autoTextField = new AutoTextField(model, field);
						widgets.add(autoTextField);
						getChildren().add(autoTextField);
					}
				});
	}

	public void updateModel() {
		widgets.forEach(Runnable::run);
	}

}

@SuppressWarnings("rawtypes")
class AutoChoiceBox extends FormGroup implements Runnable {

	private final Field field;
	private final Object model;
	private final ChoiceBox node;

	@SuppressWarnings("unchecked")
	public AutoChoiceBox(final Object model, final Field field) {
		super(new Label(StringUtil.splitCamelCase(field.getName())), new ChoiceBox(), null);
		this.model = model;
		this.field = field;
		this.node = (ChoiceBox) UiUtil.fullWidth(getNode());
		
		try {
			final Class<?> type = field.getType();
			if(HasName.class.isAssignableFrom(type)) {
				node.setConverter(new StringConverter() {
					@Override
					public String toString(Object object) {
						try {
							return (String) type.getMethod("getName").invoke(object);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
					@Override
					public Object fromString(String string) {
						try {
							return type.getMethod("findByName").invoke(null, string);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});
			}
			node.getItems().addAll(type.getEnumConstants());
			final Object selected = field.get(model);
			if(null != selected) {
				node.getSelectionModel().select(selected);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			field.set(model, node.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class AutoTextField extends FormGroup implements Runnable {

	private final Field field;
	private final Object model;
	private final TextField node;

	public AutoTextField(final Object model, final Field field) {
		super(new Label(StringUtil.splitCamelCase(field.getName())), new TextField(), null);
		this.model = model;
		this.field = field;
		this.node = (TextField) getNode();
		try {
			final Object value = field.get(model);
			node.setText(null == value ? "" : String.valueOf(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
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
			setMessage(new Message(e.getMessage(), MessageType.ERROR));
			throw new RuntimeException(e);
		}
	}

}

class AutoCheckBox extends FormGroup implements Runnable {

	private final Field field;
	private final Object model;
	private final CheckBox node;

	public AutoCheckBox(final Object model, final Field field) {
		super(new Label(StringUtil.splitCamelCase(field.getName())), new CheckBox(), null);
		this.model = model;
		this.field = field;
		this.node = (CheckBox) getNode();
		try {
			final Boolean value = (Boolean) field.get(model);
			node.setSelected(null == value ? false : value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			field.set(model, node.isSelected());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
