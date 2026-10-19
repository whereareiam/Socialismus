package me.whereareiam.socialismus.module.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.resource.ResourceRequirement;
import me.whereareiam.socialismus.model.update.UpdateConfiguration;

import java.util.List;

/**
 * Represents a module configuration in the Socialismus plugin system.
 * A module contains metadata about its functionality, compatibility,
 * and dependencies.
 *
 * <p>This class uses Lombok annotations for boilerplate code generation
 * and implements the Builder pattern for flexible object creation.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Module {
	/**
	 * The name of the module
	 */
	private String name;

	/**
	 * The version string of the module
	 */
	private String version;

	/**
	 * List of module authors
	 */
	private List<String> authors;

	/**
	 * The versions of Socialismus the module is built for, as a regular expression the whole version must match. The
	 * build fills it with every release, release candidate and development build.
	 */
	private String socialismus;

	/**
	 * The server platforms the module runs on; it loads on any one of them.
	 */
	@Builder.Default
	private List<ModulePlatform> platforms = List.of();

	/**
	 * What the module needs: other modules, integrations, and what the platform loaded
	 */
	@Builder.Default
	private List<ModuleDependency> dependencies = List.of();

	/**
	 * List of resource requirements for this module
	 * (e.g., {@code socialismus:sync})
	 */
	@Builder.Default
	private List<ResourceRequirement> requirements = List.of();

	/**
	 * Updater configuration for this module.
	 */
	private UpdateConfiguration updater;

	/**
	 * The packages the modules depending on this one load from it, each with its subpackages: its API, and the
	 * libraries it shares with them, such as {@code me.whereareiam.socialismus.module.database.api} and
	 * {@code org.jdbi}. Every other class of the module stays its own. The build writes them from
	 * {@code socialismusModule { exports }}.
	 */
	@Builder.Default
	private List<String> exports = List.of();

	/**
	 * The main class path of the module
	 */
	private String main;
}
