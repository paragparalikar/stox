package com.stox.core.intf;

public interface Identifiable<T> extends HasId<T> {

	void setId(final T id);

}
