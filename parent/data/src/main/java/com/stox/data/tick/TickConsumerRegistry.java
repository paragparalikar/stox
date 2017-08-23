package com.stox.data.tick;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class TickConsumerRegistry {

	private final Map<TickConsumer, TickConsumer> consumers = new WeakHashMap<>();

	public void register(final TickConsumer consumer) {
		synchronized (consumers) { 
			consumers.put(consumer, consumer);
		}
	}

	public void unregister(final TickConsumer consumer) {
		synchronized (consumers) {
			consumers.remove(consumer);
		}
	}

	public Set<TickConsumer> getTickConsumers() {
		return consumers.keySet();
	}
	
	
}
