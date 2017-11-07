package com.stox.core.ui.widget;

import com.stox.core.model.Message;
import com.stox.core.ui.widget.validator.TextValidator;
import com.stox.core.util.StringUtil;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidatingTextField extends TextField {

	private TextValidator validator;

	@Builder
	public ValidatingTextField(final String placeholder, final String initialValue, final TextValidator validator) {
		this.validator = validator;
		if (StringUtil.hasText(placeholder)) {
			setPromptText(placeholder);
		}
		if (StringUtil.hasText(initialValue)) {
			setText(initialValue);
		}
		textProperty().addListener((observable, oldValue, newValue) -> {
			if(null != this.validator) {
				final Message message = this.validator.validate();
				if(null != message) {
					final Tooltip tooltip = new Tooltip(message.getText());
					setTooltip(tooltip);
					getStyleClass().removeAll("success", "warning", "info", "danger");
					getStyleClass().add(message.getType().getStyle());
				}
			}
		});
	}

}
