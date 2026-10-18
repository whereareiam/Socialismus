package me.whereareiam.socialismus.testing.environment;

import org.jetbrains.annotations.NotNull;

/**
 * A module jar the build alters for a journey about the module system, installed in {@code modules/} like a real
 * module.
 */
public enum ModuleFixture {
	/** Chirper's jar with a {@code module.json} that asks for Socialismus 1.0.0. */
	REQUIRES_ANOTHER_VERSION("module-requires-another-version", "Chirper");

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
