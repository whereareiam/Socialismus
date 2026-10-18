package me.whereareiam.socialismus.testing.environment;

import org.jetbrains.annotations.NotNull;

/**
 * A Socialismus module a journey installs in {@code modules/}, by the Anvil artifact of its jar.
 */
public enum Module {
	CHIRPER("chirper", "Chirper"),
	ESSENTIALS("essentials", "Essentials"),
	BUBBLER("bubbler", "Bubbler");

	private final String artifact;
	private final String name;

	Module(String artifact, String name) {
		this.artifact = artifact;
		this.name = name;
	}

	@NotNull
	String artifact() {
		return artifact;
	}

	/**
	 * Returns the name the module gives itself, which also names its directory in {@code modules/}.
	 */
	@NotNull
	public String moduleName() {
		return name;
	}
}
