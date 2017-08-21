package com.stox.watchlist.ui;


import java.util.function.Consumer;

import com.stox.core.ui.util.Icon;
import com.stox.core.ui.util.UiUtil;
import com.stox.core.ui.widget.TableViewSearchTextField;
import com.stox.watchlist.model.Watchlist;
import com.stox.watchlist.model.WatchlistEntry;
import com.stox.watchlist.model.WatchlistEntryMatcher;
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
public class WatchlistView extends View {
	
	private final Button addButton = UiUtil.classes(new Button(Icon.PLUS), "icon");
	private final Button editButton = UiUtil.classes(new Button(Icon.PENCIL), "icon");
	private final Button deleteButton = UiUtil.classes(new Button(Icon.MINUS), "icon");
	private final ComboBox<Watchlist> watchlistComboBox = UiUtil.fullWidth(new ComboBox<>());
	
	private final TableView<WatchlistEntry> entryTableView = new TableView<>();
	
	private final ToggleButton searchButton = UiUtil.classes(new ToggleButton(Icon.SEARCH), "icon", "primary");
	private final TextField searchTextField = new TableViewSearchTextField<WatchlistEntry>(entryTableView, new WatchlistEntryMatcher());
	
	private Consumer<WatchlistEntry> deleteConsumer;
	
	public WatchlistView() {
		super(WatchlistUiConstant.CODE, WatchlistUiConstant.NAME, WatchlistUiConstant.ICON);
		getTitleBar().add(Side.RIGHT, 0, searchButton);
		setContent(new BorderPane(entryTableView, UiUtil.classes(new HBox(watchlistComboBox, addButton, editButton, deleteButton),"watchlist-combobox-panel"), null, null, null));
		createColumns();
	}
	
	private void createColumns() {
		createDeleteColumn();
		createNameColumn();
		createBarSpanColumn();
	}
	
	private void createDeleteColumn() {
		final TableColumn<WatchlistEntry, WatchlistEntry> column = new TableColumn<WatchlistEntry, WatchlistEntry>();
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WatchlistEntry,WatchlistEntry>, ObservableValue<WatchlistEntry>>() {
			@Override
			public ObservableValue<WatchlistEntry> call(CellDataFeatures<WatchlistEntry, WatchlistEntry> param) {
				return ObjectConstant.valueOf(param.getValue());
			}
		});
		column.setCellFactory(new Callback<TableColumn<WatchlistEntry,WatchlistEntry>, TableCell<WatchlistEntry,WatchlistEntry>>() {
			@Override
			public TableCell<WatchlistEntry, WatchlistEntry> call(TableColumn<WatchlistEntry, WatchlistEntry> param) {
				return new TableCell<WatchlistEntry, WatchlistEntry>(){
					private final Button deleteButton = UiUtil.classes(new Button(Icon.CROSS), "icon");
					{
						deleteButton.addEventHandler(ActionEvent.ACTION, event -> {
							final WatchlistEntry entry = (WatchlistEntry)deleteButton.getUserData();
							if(null != deleteConsumer && null != entry) {
								deleteConsumer.accept(entry);
							}
						});
					}
					@Override
					protected void updateItem(WatchlistEntry item, boolean empty) {
						setGraphic(empty ? null : deleteButton);
						deleteButton.setUserData(empty ? null : item);
						super.updateItem(item, empty);
					}
					
				};
			}
		});
		column.setPrefWidth(70);
		entryTableView.getColumns().add(column);
	}
	
	private void createNameColumn() {
		final TableColumn<WatchlistEntry, String> column = new TableColumn<WatchlistEntry, String>("Name");
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WatchlistEntry, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<WatchlistEntry, String> param) {
				if (null != param && null != param.getValue() && null != param.getValue().getInstrument() && null != param.getValue().getInstrument().getName()) {
					return StringConstant.valueOf(param.getValue().getInstrument().getName());
				}
				return null;
			}
		});
		column.setPrefWidth(120);
		entryTableView.getColumns().add(column);
	}

	private void createBarSpanColumn() {
		final TableColumn<WatchlistEntry, String> column = new TableColumn<WatchlistEntry, String>("Timeframe");
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WatchlistEntry, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<WatchlistEntry, String> param) {
				if (null != param && null != param.getValue() && null != param.getValue().getBarSpan() && null != param.getValue().getBarSpan().getName()) {
					return StringConstant.valueOf(param.getValue().getBarSpan().getName());
				}
				return null;
			}
		});
		entryTableView.getColumns().add(column);
	}
}