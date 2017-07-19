package com.stox.workbench.model;

import lombok.Data;

@Data
public abstract class ViewState {

	private String code;

	private int linkOrdinal;

	private double x;

	private double y;

	private double width;

	private double height;

}
