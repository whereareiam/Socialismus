package me.whereareiam.socialismus.api.type;

import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Enumeration representing the type of plugin implementation being used.
 * This enum helps identify and manage different server platforms that
 * the plugin can run on.
 */
public enum PluginType {
	/**
	 * Represents an unknown or unsupported plugin type
	 */
	UNKNOWN,
	/**
	 * Represents a Spigot plugin implementation
	 */
	BUKKIT,
	/**
	 * Represents a Paper plugin implementation
	 */
	PAPER,
	/**
	 * Represents a Velocity proxy plugin implementation
	 */
	VELOCITY;

	/**
	 * The current plugin type set during runtime
	 */
	@Setter
	private static PluginType pluginType = UNKNOWN;

	/**
	 * Returns the exact plugin type that was set during runtime.
	 * This method returns the cached plugin type without performing
	 * any manifest checks.
	 *
	 * @return The current {@link PluginType} of the running plugin
	 */
	public static PluginType getExactType() {
		return pluginType;
	}

	/**
	 * Determines the plugin type by reading the manifest file.
	 * This method checks the Plugin-Type attribute in the JAR's manifest.
	 *
	 * @return The {@link PluginType} based on the manifest information
	 * @throws IllegalStateException if the plugin type cannot be determined
	 */
	public static PluginType getType() {
		String pluginType = getPluginTypeFromManifest();
		if (pluginType != null)
			return PluginType.valueOf(pluginType);

		throw new IllegalStateException("Unknown plugin type");
	}

	/**
	 * Reads the Plugin-Type attribute from the JAR's manifest file.
	 *
	 * @return The plugin type string from manifest, or null if not found
	 */
	private static String getPluginTypeFromManifest() {
		try (JarFile jarFile = new JarFile(new File(PluginType.class.getProtectionDomain().getCodeSource().getLocation().toURI()))) {
			Manifest manifest = jarFile.getManifest();
			Attributes attributes = manifest.getMainAttributes();

			return attributes.getValue("Plugin-Type");
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
}