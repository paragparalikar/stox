package com.stox.core.intf;

import java.io.DataInput;
import java.io.IOException;

public interface DataReader {

	public void read(DataInput input) throws IOException;

}
