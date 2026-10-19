package me.whereareiam.socialismus.testing.environment;

import org.jetbrains.annotations.NotNull;

/**
 * A module jar the build makes or alters for a journey about the module system, installed in {@code modules/} like a
 * real module.
 */
public enum ModuleFixture {
	/** Chirper's jar with a {@code module.json} that asks for Socialismus 1.0.0. */
	REQUIRES_ANOTHER_VERSION("module-requires-another-version", "Chirper"),

	/** Chirper's jar with a {@code module.json} that runs only on Velocity. */
	FOR_ANOTHER_PLATFORM("module-for-another-platform", "Chirper"),

	/** A module that exports one package and keeps another to itself. */
	JOURNEY_LIBRARY("module-journey-library", "JourneyLibrary"),

	/**
	 * A module that depends on {@link #JOURNEY_LIBRARY} and on the PacketEvents plugin, and logs what it reaches of
	 * them and whether the library still serves it while it stops.
	 */
	JOURNEY_CONSUMER("module-journey-consumer", "JourneyConsumer");

	private final String artifact;
	private final String name;

	ModuleFixture(String artifact, String name) {
		this.artifact = artifact;
		this.name = name;
	}

	@NotNull
	String artifact() {
		return artifact;
	}

	/**
	 * Returns the name the altered module gives itself.
	 */
	@NotNull
	public String moduleName() {
		return name;
	}
}
