package me.whereareiam.socialismus.module.model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * A plugin of the platform that a module depends on.
 *
 * <p>The platform supplies the class loader rather than the module, so the
 * module uses the very classes the installed plugin uses.</p>
 */
@Value
public class PlatformDependency {
	@NotNull String name;
	@NotNull String version;
	@NotNull ClassLoader classLoader;
}
