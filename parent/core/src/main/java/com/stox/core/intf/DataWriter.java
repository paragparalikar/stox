package com.stox.core.intf;

import java.io.DataOutput;
import java.io.IOException;

public interface DataWriter {

	public void write(final DataOutput output) throws IOException;

}
