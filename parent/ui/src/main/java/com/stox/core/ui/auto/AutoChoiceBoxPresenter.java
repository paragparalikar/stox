package com.stox.core.ui.auto;

import java.lang.reflect.Field;

import com.stox.core.intf.HasName;
import com.stox.core.ui.Container;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.StringUtil;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

@SuppressWarnings("rawtypes")
public class AutoChoiceBoxPresenter implements AutoPresenter {

	private final Field field;
	private final Object model;
	private final Label label = new Label();
	private final ChoiceBox node = new ChoiceBox();
	
	private final HBox hBox = new HBox(label, UiUtil.spacer(), node);

	public AutoChoiceBoxPresenter(final Field field, final Object model) {
		this.field = field;
		this.model = model;
		node.setPrefWidth(100);
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
			field.set(model, node.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public void updateView() {
		try {
			final Class<?> type = field.getType();
			if (HasName.class.isAssignableFrom(type)) {
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
			if (null != selected) {
				node.getSelectionModel().select(selected);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
