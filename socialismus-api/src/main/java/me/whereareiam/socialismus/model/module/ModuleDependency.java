package me.whereareiam.socialismus.model.module;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.type.module.DependencyType;

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
}