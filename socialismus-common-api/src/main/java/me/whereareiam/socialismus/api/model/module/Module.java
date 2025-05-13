package me.whereareiam.socialismus.api.model.module;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.type.Version;

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
	 * The main class path of the module
	 */
	private String main;
}