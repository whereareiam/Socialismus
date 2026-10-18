package me.whereareiam.socialismus.testing.environment;

import org.jetbrains.annotations.NotNull;

/**
 * A plugin Socialismus integrates with, which a journey installs in the server's {@code plugins/} beside
 * Socialismus, by the Anvil artifact of its jar.
 */
public enum Plugin {
	/** PacketEvents, which Bubbler draws its bubbles with. */
	PACKETEVENTS("packetevents", "packetevents");

	private final String artifact;
	private final String fileName;

	Plugin(String artifact, String fileName) {
		this.artifact = artifact;
		this.fileName = fileName;
	}

	@NotNull
	String artifact() {
		return artifact;
	}

	/**
	 * Returns the name of the plugin's jar in {@code plugins/}, without its extension.
	 */
	@NotNull
	String fileName() {
		return fileName;
	}
}
