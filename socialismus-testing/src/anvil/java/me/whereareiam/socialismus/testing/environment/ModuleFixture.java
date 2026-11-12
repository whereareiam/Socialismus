package me.whereareiam.socialismus.testing.environment;

import org.jetbrains.annotations.NotNull;

/**
 * A module jar the build makes or alters for a journey about the module system, installed in {@code modules/} like a
 * real module.
 */
public enum ModuleFixture {
	/** Chirper's jar with a {@code module.yml} whose host range accepts only Socialismus 1.x. */
	REQUIRES_ANOTHER_VERSION("module-requires-another-version", "chirper"),

	/** Chirper's jar with a {@code module.yml} that runs only on Velocity. */
	FOR_ANOTHER_PLATFORM("module-for-another-platform", "chirper"),

	/** A module that exports one package and keeps another to itself. */
	JOURNEY_LIBRARY("module-journey-library", "journey-library"),

	/**
	 * A module that depends on {@link #JOURNEY_LIBRARY} and on the PacketEvents plugin, and logs what it reaches of
	 * them and whether the library still serves it while it stops.
	 */
	JOURNEY_CONSUMER("module-journey-consumer", "journey-consumer");

	private final String artifact;
	private final String id;

	ModuleFixture(String artifact, String id) {
		this.artifact = artifact;
		this.id = id;
	}

	@NotNull
	String artifact() {
		return artifact;
	}

	/**
	 * Returns the id the module gives itself in its {@code module.yml}.
	 */
	@NotNull
	public String id() {
		return id;
	}
}
