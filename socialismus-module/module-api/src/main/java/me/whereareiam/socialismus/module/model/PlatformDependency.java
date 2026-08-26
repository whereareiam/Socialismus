package me.whereareiam.socialismus.module.model;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * A platform component that can expose an API to a Socialismus module.
 *
 * <p>The class loader is deliberately supplied by the platform rather than by
 * a module. This preserves type identity with the installed plugin while
 * allowing the module loader to delegate only declared API packages.</p>
 */
@Value
public class PlatformDependency {
	@NotNull String name;
	@NotNull String version;
	@NotNull ClassLoader classLoader;
}
