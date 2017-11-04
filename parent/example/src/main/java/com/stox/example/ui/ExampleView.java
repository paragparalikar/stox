package com.stox.example.ui;


import java.util.function.Consumer;

import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.TableViewSearchTextField;
import com.stox.core.util.Constant;
import com.stox.example.model.Example;
import com.stox.example.model.ExampleGroup;
import com.stox.example.model.ExampleMatcher;
import com.stox.workbench.ui.view.View;
import com.sun.javafx.binding.ObjectConstant;
import com.sun.javafx.binding.StringConstant;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ExampleView extends View {
	
	private final Button addButton = UiUtil.classes(new Button(Icon.PLUS), "icon");
	private final Button editButton = UiUtil.classes(new Button(Icon.PENCIL), "icon");
	private final Button deleteButton = UiUtil.classes(new Button(Icon.MINUS), "icon");
	private final ComboBox<ExampleGroup> exampleGroupComboBox = UiUtil.fullWidth(new ComboBox<>());
	
	private final TableView<Example> exampleTableView = new TableView<>();
	
	private final ToggleButton searchButton = UiUtil.classes(new ToggleButton(Icon.SEARCH), "icon", "primary");
	private final TextField searchTextField = new TableViewSearchTextField<Example>(exampleTableView, new ExampleMatcher());
	
	private Consumer<Example> deleteConsumer;
	
	public ExampleView() {
		super(ExampleUiConstant.CODE, ExampleUiConstant.NAME, ExampleUiConstant.ICON);
		getTitleBar().add(Side.RIGHT, 0, searchButton);
		add(new BorderPane(exampleTableView, UiUtil.classes(new HBox(exampleGroupComboBox, addButton, editButton, deleteButton),"example-group-combobox-panel"), null, null, null));
		createColumns();
	}
	
	private void createColumns() {
		exampleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		createDeleteColumn();
		createNameColumn();
		createBarSpanColumn();
		createDateColumn();
	}
	
	private void createDeleteColumn() {
		final TableColumn<Example, Example> column = new TableColumn<Example, Example>();
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Example,Example>, ObservableValue<Example>>() {
			@Override
			public ObservableValue<Example> call(CellDataFeatures<Example, Example> param) {
				return ObjectConstant.valueOf(param.getValue());
			}
		});
		column.setCellFactory(new Callback<TableColumn<Example,Example>, TableCell<Example,Example>>() {
			@Override
			public TableCell<Example, Example> call(TableColumn<Example, Example> param) {
				return new TableCell<Example, Example>(){
					private final Button deleteButton = UiUtil.classes(new Button(Icon.CROSS), "icon");
					{
						deleteButton.addEventHandler(ActionEvent.ACTION, event -> {
							final Example entry = (Example)deleteButton.getUserData();
							if(null != deleteConsumer && null != entry) {
								deleteConsumer.accept(entry);
							}
						});
					}
					@Override
					protected void updateItem(Example item, boolean empty) {
						setGraphic(empty ? null : deleteButton);
						deleteButton.setUserData(empty ? null : item);
						super.updateItem(item, empty);
					}
					
				};
			}
		});
		column.setMinWidth(40);
		column.setPrefWidth(40);
		column.setMaxWidth(40);
		exampleTableView.getColumns().add(column);
	}
	
	private void createNameColumn() {
		final TableColumn<Example, String> column = new TableColumn<Example, String>("Name");
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Example, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Example, String> param) {
				if (null != param && null != param.getValue() && null != param.getValue().getInstrument() && null != param.getValue().getInstrument().getName()) {
					return StringConstant.valueOf(param.getValue().getInstrument().getName());
				}
				return null;
			}
		});
		exampleTableView.getColumns().add(column);
	}

	private void createBarSpanColumn() {
		final TableColumn<Example, String> column = new TableColumn<Example, String>("Timeframe");
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Example, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Example, String> param) {
				if (null != param && null != param.getValue() && null != param.getValue().getBarSpan() && null != param.getValue().getBarSpan().getName()) {
					return StringConstant.valueOf(param.getValue().getBarSpan().getName());
				}
				return null;
			}
		});
		column.setMinWidth(100);
		column.setPrefWidth(100);
		column.setMaxWidth(100);
		exampleTableView.getColumns().add(column);
	}
	
	private void createDateColumn() {
		final TableColumn<Example, String> column = new TableColumn<Example, String>("Date");
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Example, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<Example, String> param) {
				if (null != param && null != param.getValue() && null != param.getValue().getDate()) {
					return StringConstant.valueOf(Constant.dateFormatFull.format(param.getValue().getDate()));
				}
				return null;
			}
		});
		column.setMinWidth(200);
		column.setPrefWidth(200);
		column.setMaxWidth(200);
		exampleTableView.getColumns().add(column);
	}
}