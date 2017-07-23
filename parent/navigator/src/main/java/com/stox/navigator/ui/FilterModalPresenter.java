package com.stox.navigator.ui;

import javafx.event.ActionEvent;

import com.stox.data.ui.FilterPresenter;

public class FilterModalPresenter {

	private final FilterModal modal = new FilterModal();

	public FilterModalPresenter(final FilterPresenter filterPresenter) {
		modal.setContent(filterPresenter.getView());
		modal.addStylesheets(filterPresenter.getStylesheets());
		modal.getCancelButton().addEventHandler(ActionEvent.ACTION, event -> modal.hide());
		modal.getFilterButton().addEventHandler(ActionEvent.ACTION, event -> {
			filterPresenter.filter();
			modal.hide();
		});
	}

	public FilterModal getModal() {
		return modal;
	}

}
