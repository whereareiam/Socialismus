package me.whereareiam.socialismus.module.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.ResourceRequirement;
import me.whereareiam.socialismus.model.update.UpdateConfiguration;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.Version;

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
	 * List of platforms this module supports
	 */
	private List<PlatformType> supportedPlatforms;

	/**
	 * List of game versions this module supports
	 */
	private List<Version> supportedVersions;

	/**
	 * List of other modules this module depends on
	 */
	private List<ModuleDependency> dependencies;

	/**
	 * List of resource requirements for this module
	 * (e.g., database)
	 */
	@Builder.Default
	private List<ResourceRequirement> requirements = List.of();

	/**
	 * Updater configuration for this module.
	 */
	private UpdateConfiguration updater;

	/**
	 * The main class path of the module
	 */
	private String main;
}
