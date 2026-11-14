package me.whereareiam.socialismus.testing.environment;

import org.jetbrains.annotations.NotNull;

/**
 * A Socialismus module a journey installs in {@code modules/}, by the Anvil artifact of its jar.
 */
public enum Module {
	CHIRPER("chirper", "chirper"),
	DIALOGUE("dialogue", "dialogue"),
	MENTIONATOR("mentionator", "mentionator"),
	ESSENTIALS("essentials", "essentials"),
	BUBBLER("bubbler", "bubbler"),
	REDIS("redis", "socialismus-redis"),
	DATABASE("database", "socialismus-database"),
	NETWORK("network", "socialismus-network"),
	CHANNELIZER("channelizer", "socialismus-channelizer"),
	LINK("link", "socialismus-link");

	private final String artifact;
	private final String id;

	Module(String artifact, String id) {
		this.artifact = artifact;
		this.id = id;
	}

	@NotNull
	String artifact() {
		return artifact;
	}

	/**
	 * Returns the id the module gives itself in its {@code module.yml}, which also names its directory in
	 * {@code modules/}.
	 */
	@NotNull
	public String id() {
		return id;
	}
}
