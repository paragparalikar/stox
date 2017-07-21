package com.stox.workbench.ui.stage;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.text.Font;
import javafx.stage.Screen;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.stox.core.intf.HasLifecycle;
import com.stox.core.ui.ToastCallback;
import com.stox.workbench.client.WorkbenchClient;
import com.stox.workbench.model.WorkbenchState;
import com.stox.workbench.ui.view.Link;
import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.PresenterProvider;
import com.stox.workbench.ui.view.View;
import com.stox.workbench.ui.view.event.RemoveViewRequestEvent;

@Component
public class WorkbenchPresenter implements HasLifecycle {

	static {
		final String[] fonts = { "awesome/fontawesome-webfont.ttf", "open-sans/OpenSans-Bold.ttf", "open-sans/OpenSans-BoldItalic.ttf", "open-sans/OpenSans-ExtraBold.ttf",
				"open-sans/OpenSans-ExtraBoldItalic.ttf", "open-sans/OpenSans-Italic.ttf", "open-sans/OpenSans-Light.ttf", "open-sans/OpenSans-LightItalic.ttf",
				"open-sans/OpenSans-Regular.ttf", "open-sans/OpenSans-SemiBold.ttf", "open-sans/OpenSans-SemiBoldItalic.ttf" };
		for (final String font : fonts) {
			Font.loadFont(Workbench.class.getClassLoader().getResource("fonts/" + font).toExternalForm(), 14);
		}
	}

	private boolean maximized = false;
	private Rectangle2D backupBounds = null;
	private final List<Presenter<?, ?>> presenters = new LinkedList<Presenter<?, ?>>();
	private final Workbench workbench = new Workbench();

	@Autowired
	private WorkbenchClient workbenchClient;

	public Workbench getWorkbench() {
		return workbench;
	}

	private void style() {
		final List<String> stylesheets = workbench.getScene().getStylesheets();
		stylesheets.clear();
		stylesheets.addAll(Arrays.asList("styles/color-sceme.css", "styles/bootstrap.css", "styles/common.css", "styles/workbench.css"));
	}

	private void bind() {
		workbench.getTitleBar().getMinimizeButton().addEventHandler(ActionEvent.ACTION, event -> workbench.setIconified(true));
		workbench.getTitleBar().getMaximizeButton().addEventHandler(ActionEvent.ACTION, event -> toggleMaximized());
		workbench.getTitleBar().getCloseButton().addEventHandler(ActionEvent.ACTION, event -> Platform.exit());
	}

	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshed(final ContextRefreshedEvent event) {
		if (!workbench.isShowing()) {
			setSize();
			final ApplicationContext context = event.getApplicationContext();
			final Collection<PresenterProvider> providers = context.getBeansOfType(PresenterProvider.class).values();
			providers.forEach(provider -> workbench.getTitleBar().getApplicationsMenu().getItems().add(new ApplicationMenuItem(this, provider)));
			workbench.show();
			loadState(providers);
		}
	}

	@Override
	@PostConstruct
	public void start() {
		style();
		bind();
	}

	@Override
	@PreDestroy
	public void stop() {
		final WorkbenchState workbenchState = new WorkbenchState();
		presenters.forEach(presenter -> workbenchState.getViewStates().add(presenter.getViewState()));
		Arrays.asList(Link.values()).forEach(link -> workbenchState.getLinkStates().put(link, link.getState()));
		workbenchClient.save(workbenchState, new ToastCallback<Void, Void>((state) -> {
			return null;
		}));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadState(final Collection<PresenterProvider> providers) {
		workbenchClient.load(new ToastCallback<WorkbenchState, Void>((state) -> {
			state.getViewStates().forEach(
					viewState -> providers.stream().filter(provider -> provider.getViewCode().equals(viewState.getCode())).findFirst().ifPresent(provider -> {
						final Presenter presenter = provider.create();
						add(presenter);
						presenter.setViewSate(viewState);
					}));
			return null;
		}));
	}

	public void add(final Presenter<?, ?> presenter) {
		final View view = presenter.getView();
		workbench.getContentPane().getChildren().add(view);
		presenter.start();
		presenters.add(presenter);
	}

	@EventListener(RemoveViewRequestEvent.class)
	public void onRemoveViewRequest(final RemoveViewRequestEvent event) {
		workbench.getContentPane().getChildren().remove(event.getView());
		presenters.stream().filter(presenter -> presenter.getView() == event.getView()).findFirst().ifPresent(presenter -> presenters.remove(presenter));
	}

	private Rectangle2D getVisualRectangle() {
		return Screen.getPrimary().getVisualBounds();
	}

	private void setSize() {
		final Rectangle2D rectangle = getVisualRectangle();
		workbench.setWidth(rectangle.getWidth());
		workbench.setHeight(rectangle.getHeight());
		maximized = true;
	}

	public void toggleMaximized() {
		if (maximized) {
			maximized = false;
			if (null != backupBounds) {
				workbench.setX(backupBounds.getMinX());
				workbench.setY(backupBounds.getMinY());
				workbench.setWidth(backupBounds.getWidth());
				workbench.setHeight(backupBounds.getHeight());
			}
		} else {
			maximized = true;
			backupBounds = new Rectangle2D(workbench.getX(), workbench.getY(), workbench.getWidth(), workbench.getHeight());
			final Rectangle2D parentBounds = getVisualRectangle();
			workbench.setX(parentBounds.getMinX());
			workbench.setY(parentBounds.getMinY());
			workbench.setHeight(parentBounds.getMaxY() - parentBounds.getMinY());
			workbench.setWidth(parentBounds.getMaxX() - parentBounds.getMinX());
		}
	}

}
