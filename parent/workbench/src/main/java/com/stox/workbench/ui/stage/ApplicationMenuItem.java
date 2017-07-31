package com.stox.workbench.ui.stage;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import com.stox.core.ui.util.UiUtil;
import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.PresenterProvider;

public class ApplicationMenuItem extends MenuItem {

	public ApplicationMenuItem(final WorkbenchPresenter workbenchPresenter, final PresenterProvider presenterProvider) {
		super(presenterProvider.getViewName());
		setGraphic(UiUtil.classes(new Label(presenterProvider.getViewIcon()), "icon"));

		addEventHandler(ActionEvent.ACTION, event -> {
			final Presenter<?, ?> presenter = presenterProvider.create();
			workbenchPresenter.add(presenter, null);
		});
	}

}
