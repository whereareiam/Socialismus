package me.whereareiam.socialismus.lifecycle;

import org.jetbrains.annotations.NotNull;

/**
 * A part of Socialismus that starts and stops with it. Each lifecycle belongs to a {@link StartupPhase}; Socialismus
 * starts the phases in order and, within a phase, the lifecycles in the order they were bound. It stops them in the
 * reverse order.
 * <p>
 * Building a lifecycle, like building any object Socialismus injects, reads no document: documents are read when
 * the lifecycle starts or later.
 */
public interface RuntimeLifecycle {
	/**
	 * Returns the phase this lifecycle starts in.
	 *
	 * @return the lifecycle's phase
	 */
	@NotNull StartupPhase phase();

	/**
	 * Starts this lifecycle once every lifecycle of the earlier phases has started. A failure stops the startup of
	 * Socialismus: the lifecycles started before are stopped in reverse and the failure is passed on.
	 */
	void initialize();

	/**
	 * Releases registrations and resources, including those from partial initialization.
	 */
	void shutdown();
}
