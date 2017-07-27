package com.stox.navigator.ui;

import javafx.event.ActionEvent;

import com.stox.core.ui.filter.FilterPresenter;

public class FilterModalPresenter {

	private final FilterModal modal = new FilterModal();

	public FilterModalPresenter(final FilterPresenter filterPresenter) {
		modal.setContent(filterPresenter.getView());
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
