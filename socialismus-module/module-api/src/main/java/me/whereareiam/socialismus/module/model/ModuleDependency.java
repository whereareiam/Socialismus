package me.whereareiam.socialismus.module.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.type.DependencyType;

import java.util.List;

/**
 * Represents a dependency relationship between modules in the Socialismus plugin system.
 * Each dependency specifies the required module name, version, and dependency type.
 *
 * <p>This class uses Lombok annotations for boilerplate code generation
 * and implements the Builder pattern for flexible object creation.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ModuleDependency {
	/**
	 * The name of the required module
	 */
	private String name;

	/**
	 * The required version of the module
	 */
	private String version;

	/**
	 * The type of dependency relationship
	 */
	private DependencyType type;

	/**
	 * API package prefixes a {@link DependencyType#PLATFORM_COMPONENT} dependency
	 * exposes to this module. Each prefix must end with a period.
	 */
	@Builder.Default
	private List<String> apiPackages = List.of();
}
