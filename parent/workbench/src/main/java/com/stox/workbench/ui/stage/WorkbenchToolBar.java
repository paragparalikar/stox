package com.stox.workbench.ui.stage;

import javafx.scene.control.ToolBar;

import com.stox.core.ui.util.UiUtil;

public class WorkbenchToolBar extends ToolBar {

	public WorkbenchToolBar() {
		UiUtil.classes(this, "workbench-tool-bar", "right");
	}

}
