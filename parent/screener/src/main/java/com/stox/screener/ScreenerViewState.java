package com.stox.screener;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stox.core.model.Instrument;
import com.stox.workbench.model.ViewState;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(value = { "started", "running", "progress", "matches" })
public class ScreenerViewState extends ViewState{
	
	private transient boolean started = false;
	
	private transient boolean running = false;
	
	private transient double progress = 0;

	private String wizardPresenterId = ScreenerUiConstant.SCREEN_SELECTION_PRESENTER;
	
	private List<ScreenConfiguration> screenConfigurations = new ArrayList<>();
	
	private  transient ObservableList<Instrument> matches = FXCollections.observableArrayList();
	
}
