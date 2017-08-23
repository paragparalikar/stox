package com.stox.data.tick;

import java.util.function.Consumer;

import com.stox.core.intf.HasBarSpan;
import com.stox.core.intf.HasInstrument;

public interface TickConsumer extends Consumer<TickWrapper>, HasBarSpan, HasInstrument  {
	

}
