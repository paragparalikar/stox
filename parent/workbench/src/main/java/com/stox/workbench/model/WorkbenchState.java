package com.stox.workbench.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Data;

import com.stox.workbench.ui.view.Link;
import com.stox.workbench.ui.view.Link.State;

@Data
public class WorkbenchState {

	private Map<Link, State> linkStates = new HashMap<Link, State>();

	private List<ViewState> viewStates = new LinkedList<ViewState>();

}
