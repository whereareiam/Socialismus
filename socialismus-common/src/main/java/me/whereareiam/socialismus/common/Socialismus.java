package me.whereareiam.socialismus.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import me.whereareiam.socialismus.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Starts and stops Socialismus: the bound {@link RuntimeLifecycle}s, phase by phase in the order of
 * {@link StartupPhase}, and within a phase in the order they were bound. They stop in the reverse order.
 */
@Singleton
public final class Socialismus {
	private final List<RuntimeLifecycle> lifecycles;
	private final List<RuntimeLifecycle> started = new ArrayList<>();

	@Inject
	public Socialismus(@NotNull Set<RuntimeLifecycle> lifecycles) {
		this.lifecycles = lifecycles.stream()
				.sorted(Comparator.comparing(RuntimeLifecycle::phase))
				.toList();
	}

	/**
	 * Starts every lifecycle. When one fails, the ones started before it, and the failed one itself, are stopped in
	 * reverse and the failure is passed on.
	 */
	public synchronized void start() {
		try {
			for (RuntimeLifecycle lifecycle : lifecycles) {
				started.add(lifecycle);
				lifecycle.initialize();
			}
		} catch (RuntimeException | Error failure) {
			stop();
			throw failure;
		}
	}

	/**
	 * Stops the started lifecycles in reverse. A lifecycle that fails to stop is logged and the others still stop.
	 * Stopping again, or before a start, does nothing.
	 */
	public synchronized void stop() {
		for (int i = started.size() - 1; i >= 0; i--) {
			RuntimeLifecycle lifecycle = started.get(i);
			try {
				lifecycle.shutdown();
			} catch (RuntimeException failure) {
				Logger.severe("Failed to stop " + lifecycle.getClass().getSimpleName(), failure);
			}
		}
		started.clear();
	}
}
