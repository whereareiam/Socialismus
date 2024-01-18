package me.whereareiam.socialismus.core;

import com.google.inject.Singleton;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Singleton
public class Scheduler {
	private final ScheduledExecutorService scheduler;

	public Scheduler() {
		this.scheduler = Executors.newScheduledThreadPool(1);
	}

	public ScheduledFuture<?> scheduleAtDateTime(Runnable command, LocalDateTime dateTime) {
		long delay = Duration.between(LocalDateTime.now(), dateTime).toMillis();
		return scheduler.schedule(command, delay, TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit, Optional<Integer> repetitions) {
		if (repetitions.isPresent()) {
			return scheduler.scheduleAtFixedRate(new Runnable() {
				private int count = 0;

				@Override
				public void run() {
					if (count < repetitions.get()) {
						command.run();
						count++;
					} else {
						throw new RuntimeException("End of repetitions");
					}
				}
			}, initialDelay, period, unit);
		} else {
			return scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
		}
	}

	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return scheduler.schedule(command, delay, unit);
	}

	public void cancelTask(ScheduledFuture<?> future) {
		future.cancel(false);
	}

	public void shutdown() {
		scheduler.shutdownNow();
	}
}
