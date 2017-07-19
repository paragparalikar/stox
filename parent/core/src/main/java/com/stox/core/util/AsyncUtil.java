package com.stox.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AsyncUtil {
	private static final Logger log = Logger.getLogger("com.finx.core.util.AsyncUtil");

	private static class FinxTreadFactory implements ThreadFactory {

		private int count;
		private final String name;

		public FinxTreadFactory(final String namePrefix) {
			this.name = namePrefix;
		}

		@Override
		public Thread newThread(Runnable runnable) {
			return new Thread(runnable, "Finx-" + name + "-" + (++count));
		}

	}

	private static final ExecutorService executorService = Executors.newWorkStealingPool();
	private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(0, new FinxTreadFactory("scheduled"));

	public synchronized static boolean isAlive() {
		return !executorService.isShutdown() && !scheduledExecutorService.isShutdown();
	}

	public synchronized static void submit(final Runnable runnable) {
		if (!executorService.isShutdown()) {
			executorService.submit(runnable);
		}
	}

	public synchronized static void shutdown() {
		log.fine("Shutting down Finx executor service");
		executorService.shutdown();
		log.fine("Shutting down Finx scheduled executor service");
		scheduledExecutorService.shutdown();
		try {
			log.fine("Waiting for shutdown of Finx executor service with timeout of 1 minute");
			executorService.awaitTermination(1, TimeUnit.MINUTES);
			log.fine("Waiting for shutdown of Finx scheduled executor service with timeout of 1 millisecond");
			scheduledExecutorService.awaitTermination(1, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.severe(e.getMessage());
		}
	}

	public synchronized static void schedule(final Runnable task, long delay, long period) {
		scheduledExecutorService.scheduleWithFixedDelay(task, delay, period, TimeUnit.MILLISECONDS);
	}

}
