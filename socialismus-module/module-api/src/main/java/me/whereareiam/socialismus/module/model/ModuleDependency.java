package me.whereareiam.socialismus.module.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.type.DependencyType;

/**
 * Something a module needs, as {@code module.json} lists it under {@code dependencies}:
 *
 * <pre>{@code
 * "dependencies": [
 *   {"type": "MODULE", "name": "SocialismusDatabase", "optional": true},
 *   {"type": "INTEGRATION", "name": "PacketEvents"},
 *   {"type": "PLATFORM", "name": "Clans", "version": "2\\..*"}
 * ]
 * }</pre>
 *
 * A module loads only when every dependency that is not optional is there.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ModuleDependency {
	/**
	 * What the dependency points at.
	 */
	private DependencyType type;

	/**
	 * Its name: the module's, the integration's, or the plugin's as the platform knows it.
	 */
	private String name;

	/**
	 * The versions accepted, as a regular expression the whole version must match; any version when absent. An
	 * integration has no version of its own.
	 */
	private String version;

	/**
	 * Whether the dependency may be missing: the module loads without it, and when it is there, the module loads
	 * after it and reaches what it offers. The module then uses it only once it is there, such as a resource it
	 * registers.
	 */
	@Builder.Default
	private boolean optional = false;
}
