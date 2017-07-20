package com.stox.core.intf;

import java.util.Comparator;

public interface HasName {

	String getName();

	public static class HasNameComaparator<T extends HasName> implements Comparator<T> {

		@Override
		public int compare(HasName o1, HasName o2) {
			return o1 == o2 ? 0 : o1.getName().compareToIgnoreCase(o2.getName());
		}

	}

}
