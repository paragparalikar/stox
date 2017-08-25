package com.stox.example.model;

import com.stox.core.intf.HasName;
import com.stox.core.intf.Identifiable;

import lombok.Data;

@Data
public class ExampleGroup implements Identifiable<Integer>, Comparable<ExampleGroup>, HasName {
	
	private Integer id;
	
	private String name;

	@Override
	public int compareTo(ExampleGroup o) {
		return name.compareToIgnoreCase(o.getName());
	}
	
	public void copy(final ExampleGroup exampleGroup) {
		setId(exampleGroup.getId());
		setName(exampleGroup.getName());
	}

}
