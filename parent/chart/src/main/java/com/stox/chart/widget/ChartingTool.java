package com.stox.chart.widget;

import java.util.List;

import javafx.scene.Node;

import org.springframework.context.event.EventListener;

import com.stox.chart.view.ChartPresenter;
import com.stox.chart.view.ChartView;
import com.stox.workbench.ui.view.Presenter;
import com.stox.workbench.ui.view.event.ViewSelectedEvent;
import com.stox.workbench.ui.widget.Tool;

public abstract class ChartingTool implements Tool {

	private ChartView chartView;
	private List<Presenter<?, ?>> presenters;

	@EventListener(ViewSelectedEvent.class)
	public void onViewSelected(final ViewSelectedEvent event) {
		final Node node = getNode();
		if (event.getPresenter() instanceof ChartPresenter) {
			node.setDisable(false);
			chartView = (ChartView) event.getView();
			onChartViewSelected(chartView);
		} else {
			if (1 == presenters.stream().filter(presenter -> presenter instanceof ChartPresenter).count()) {
				node.setDisable(false);
				chartView = (ChartView) presenters.stream().filter(presenter -> presenter instanceof ChartPresenter).findFirst().get().getView();
				onChartViewSelected(chartView);
			} else {
				chartView = null;
				node.setDisable(true);
			}
		}
	}

	public ChartView getChartView() {
		return chartView;
	}

	public abstract void onChartViewSelected(final ChartView chartView);

	@Override
	public void setPresenters(List<Presenter<?, ?>> presenters) {
		this.presenters = presenters;
	}

}
