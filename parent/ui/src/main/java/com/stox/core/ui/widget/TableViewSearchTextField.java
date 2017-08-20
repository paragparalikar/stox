package com.stox.core.ui.widget;

import com.stox.core.ui.util.UiUtil;
import com.stox.core.util.StringUtil;

import javafx.scene.control.TableView;

public class TableViewSearchTextField<T> extends AbstractSearchTextField<T> {

	private TableView<T>	tableView;

	public TableViewSearchTextField() {
		this(null, null);
	}

	public TableViewSearchTextField(final TableView<T> tableView) {
		this(tableView, null);
	}

	public TableViewSearchTextField(final AbstractSearchTextField.Callback<T> matcher) {
		this(null, matcher);
	}

	public TableViewSearchTextField(final TableView<T> tableView, final AbstractSearchTextField.Callback<T> matcher) {
		super(matcher);
		this.tableView = tableView;
	}

	public void setTableView(TableView<T> tableView) {
		this.tableView = tableView;
	}

	@Override
	public void next() {
		final String text = getText();
		if (StringUtil.hasText(text)) {
			for (int index = tableView.getSelectionModel().getSelectedIndex() + 1; index < tableView.getItems().size(); index++) {
				if (getMatcher().call(tableView.getItems().get(index), text)) {
					tableView.getSelectionModel().select(index);
					UiUtil.scrollTo(tableView, index);
					break;
				}
			}
		}
	}

	@Override
	public void previous() {
		final String text = getText();
		if (StringUtil.hasText(text)) {
			for (int index = tableView.getSelectionModel().getSelectedIndex() - 1; index > 0; index--) {
				if (getMatcher().call(tableView.getItems().get(index), text)) {
					tableView.getSelectionModel().select(index);
					UiUtil.scrollTo(tableView, index);
					break;
				}
			}
		}
	}

}
