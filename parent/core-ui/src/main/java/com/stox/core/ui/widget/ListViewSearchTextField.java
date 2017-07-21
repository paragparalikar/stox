package com.stox.core.ui.widget;

import javafx.scene.control.ListView;

import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.StringUtil;

public class ListViewSearchTextField<T> extends AbstractSearchTextField<T> {

	private ListView<T> listView;

	public ListViewSearchTextField() {
		this(null, null);
	}

	public ListViewSearchTextField(final ListView<T> listView) {
		this(listView, null);
	}

	public ListViewSearchTextField(final AbstractSearchTextField.Callback<T> matcher) {
		this(null, matcher);
	}

	public ListViewSearchTextField(final ListView<T> listView, final AbstractSearchTextField.Callback<T> matcher) {
		super(matcher);
		this.listView = listView;
	}

	public void setListView(ListView<T> listView) {
		this.listView = listView;
	}

	@Override
	public void next() {
		final String text = getText();
		if (StringUtil.hasText(text)) {
			for (int index = listView.getSelectionModel().getSelectedIndex() + 1; index < listView.getItems().size(); index++) {
				if (getMatcher().call(listView.getItems().get(index), text)) {
					listView.getSelectionModel().select(index);
					UiUtil.scrollTo(listView, index);
					break;
				}
			}
		}
	}

	@Override
	public void previous() {
		final String text = getText();
		if (StringUtil.hasText(text)) {
			for (int index = listView.getSelectionModel().getSelectedIndex() - 1; index > 0; index--) {
				if (getMatcher().call(listView.getItems().get(index), text)) {
					listView.getSelectionModel().select(index);
					UiUtil.scrollTo(listView, index);
					break;
				}
			}
		}
	}

}
