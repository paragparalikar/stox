package com.stox.screener;

import com.stox.workbench.model.ViewState;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ScreenerViewState extends ViewState{

	private String state;
	
	private String wizardPresenterCode = ScreenerUiConstant.SCREEN_SELECTION_PRESENTER;
	
}
