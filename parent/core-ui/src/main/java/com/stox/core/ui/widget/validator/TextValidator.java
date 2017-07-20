package com.stox.core.ui.widget.validator;

import java.util.regex.Pattern;

import javafx.scene.control.TextInputControl;

import com.stox.core.model.Message;
import com.stox.core.model.MessageType;
import com.stox.core.ui.widget.FormGroup.Validator;
import com.stox.core.util.StringUtil;

public class TextValidator implements Validator {

	private Pattern pattern;
	private boolean required = true;
	private int min = 0, max = Integer.MAX_VALUE;
	private String requiredMessage = "This is a required field", lengthMessage, patternMessage;
	private final TextInputControl textInputControl;

	public TextValidator(final TextInputControl textInputControl) {
		this(textInputControl, true);
	}

	public TextValidator(final TextInputControl textInputControl, final boolean required) {
		this(textInputControl, required, 0, Integer.MAX_VALUE, null);
	}

	public TextValidator(final TextInputControl textInputControl, final boolean required, final int min, final int max) {
		this(textInputControl, required, min, max, null);
	}

	public TextValidator(final TextInputControl textInputControl, final boolean required, final int min, final int max, final String pattern) {
		this.textInputControl = textInputControl;
		this.required = required;
		this.min = min;
		this.max = max;
		this.pattern = null == pattern ? null : Pattern.compile(pattern);
	}

	public TextValidator(Pattern pattern, boolean required, int min, int max, String requiredMessage, String lengthMessage, String patternMessage, TextInputControl textInputControl) {
		super();
		this.pattern = pattern;
		this.required = required;
		this.min = min;
		this.max = max;
		this.requiredMessage = requiredMessage;
		this.lengthMessage = lengthMessage;
		this.patternMessage = patternMessage;
		this.textInputControl = textInputControl;
	}

	private String getLengthMessage() {
		return null == lengthMessage ? "Value must be minimum " + min + " and maximum " + max + " characters long" : lengthMessage;
	}

	@Override
	public Message validate() {
		final String text = textInputControl.getText();
		if (required && !StringUtil.hasText(text)) {
			return new Message(requiredMessage, MessageType.ERROR);
		}
		if (min > text.length() || max < text.length()) {
			return new Message(getLengthMessage(), MessageType.ERROR);
		}
		if (null != pattern && !pattern.matcher(text).matches()) {
			return new Message(patternMessage, MessageType.ERROR);
		}
		return null;
	}

	public TextValidator requiredMessage(final String requiredMessage) {
		this.requiredMessage = requiredMessage;
		return this;
	}

	public TextValidator lengthMessage(final String lengthMessage) {
		this.lengthMessage = lengthMessage;
		return this;
	}

	public TextValidator patternMessage(final String patternMessage) {
		this.patternMessage = patternMessage;
		return this;
	}

}
