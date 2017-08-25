package com.stox.data.tick;

import java.util.function.Consumer;

import com.stox.core.intf.HasInstrument;
import com.stox.core.model.Tick;

public interface TickConsumer extends Consumer<Tick>, HasInstrument  {
	

}
