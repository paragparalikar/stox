package com.stox.screener;

import java.util.ArrayList;
import java.util.List;

import com.stox.core.model.Instrument;
import com.stox.workbench.model.ViewState;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ScreenerViewState extends ViewState{

	private String wizardPresenterId = ScreenerUiConstant.SCREEN_SELECTION_PRESENTER;
	
	private List<ScreenConfiguration> screenConfigurations = new ArrayList<>();
	
	private List<Instrument> matches = new ArrayList<>();
	
}
