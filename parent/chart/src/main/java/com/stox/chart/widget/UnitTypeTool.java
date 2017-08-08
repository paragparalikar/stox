package com.stox.chart.widget;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import org.springframework.stereotype.Component;

import com.stox.chart.plot.PrimaryPricePlot;
import com.stox.chart.unit.UnitType;
import com.stox.chart.view.ChartView;
import com.stox.core.ui.util.UiUtil;

@Component
public class UnitTypeTool extends ChartingTool {

	private final ToggleGroup toggleGroup = new ToggleGroup();
	private final ToggleButton lineButton = createButton(UnitType.LINE);
	private final ToggleButton areaButton = createButton(UnitType.AREA);
	private final ToggleButton hlcButton = createButton(UnitType.HLC);
	private final ToggleButton ohlcButton = createButton(UnitType.OHLC);
	private final ToggleButton candleButton = createButton(UnitType.CANDLE);
	private final HBox container = UiUtil.box(new HBox(lineButton, areaButton, hlcButton, ohlcButton, candleButton));

	public UnitTypeTool() {
		toggleGroup.selectedToggleProperty().addListener((observable, old, toggle) -> {
			final ChartView chartView = getChartView();
			if (null != chartView) {
				final UnitType type = (UnitType) toggle.getUserData();
				chartView.setUnitType(type);
				final PrimaryPricePlot plot = chartView.getPrimaryChart().getPrimaryPricePlot();
				plot.clearUnits();
				plot.createUnits(0, plot.getModels().size());
				plot.setDirty();
			}
		});
	}

	@Override
	public Node getNode() {
		return container;
	}

	@Override
	public void onChartViewSelected(ChartView chartView) {
		if (null != chartView) {
			for (final Toggle toggle : toggleGroup.getToggles()) {
				if (chartView.getUnitType().equals(toggle.getUserData())) {
					toggleGroup.selectToggle(toggle);
					break;
				}
			}
		}
	}

	private ToggleButton createButton(final UnitType type) {
		final ToggleButton button = new ToggleButton();
		button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		button.setGraphic(createGraphic(type));
		button.getStyleClass().add("primary");
		button.setToggleGroup(toggleGroup);
		button.setUserData(type);
		return button;
	}

	private Node createGraphic(final UnitType unitType) {
		switch (unitType) {
		case CANDLE:
			final Line wick = new Line(5, 0, 5, 10);
			final Region body = new Region();
			final Group graphic = new Group(wick, body);
			graphic.getStyleClass().add("unit-type-graphic");
			body.setLayoutX(2);
			body.setLayoutY(3);
			body.setMinHeight(4);
			body.setMinWidth(6);
			body.setStyle("-fx-background-color:white;");
			wick.setStrokeWidth(2);
			wick.setStroke(Color.WHITE);
			return graphic;
		case HLC:
			final Line line2 = new Line(0, 0, 0, 10);
			final Line line3 = new Line(0, 3, 5, 3);
			final Group hlc = new Group(line2, line3);
			hlc.getStyleClass().add("unit-type-graphic");
			line2.setStrokeWidth(2);
			line2.setStroke(Color.WHITE);
			line3.setStrokeWidth(2);
			line3.setStroke(Color.WHITE);
			return hlc;
		case LINE:
			final Line line = new Line(0, 10, 10, 0);
			line.getStyleClass().add("unit-type-graphic");
			line.setStrokeWidth(2);
			line.setStroke(Color.WHITE);
			return line;
		case AREA:
			final Polygon polygon = new Polygon(0, 7, 10, 0, 10, 10, 0, 10);
			polygon.setFill(Color.WHITE);
			polygon.getStyleClass().add("unit-type-graphic");
			return polygon;
		case OHLC:
			final Line line1 = new Line(0, 7, 5, 7);
			final Line lineO = new Line(5, 0, 5, 10);
			final Line lineC = new Line(5, 3, 10, 3);
			line1.setStrokeWidth(2);
			line1.setStroke(Color.WHITE);
			lineO.setStrokeWidth(2);
			lineO.setStroke(Color.WHITE);
			lineC.setStrokeWidth(2);
			lineC.setStroke(Color.WHITE);
			final Group ohlc = new Group(line1, lineO, lineC);
			ohlc.getStyleClass().add("unit-type-graphic");
			return ohlc;
		default:
			throw new IllegalArgumentException();
		}
	}

}
