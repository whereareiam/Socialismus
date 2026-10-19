package me.whereareiam.socialismus.module;

import me.whereareiam.socialismus.module.model.PlatformDependency;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Platform-specific class loader interface for the Socialismus plugin system.
 * This interface provides access to the appropriate ClassLoader based on the
 * platform environment (e.g., Bukkit, Velocity).
 *
 * <p>Implementations of this interface handle platform-specific class loading
 * requirements and ensure proper integration with different server platforms.</p>
 */
public interface PlatformClassLoader {
    /**
     * Retrieves the platform-specific ClassLoader instance.
     *
     * @return The ClassLoader appropriate for the current platform
     */
    ClassLoader getClassLoader();

    /**
     * Finds an enabled plugin of the platform that a module names in a
     * {@code PLATFORM} dependency.
     *
     * <p>Platforms that do not support plugin dependencies return an empty
     * result.</p>
     *
     * @param name the plugin's name as the platform knows it
     * @return the plugin's name, version and class loader, if it is enabled
     */
    default @NotNull Optional<PlatformDependency> findDependency(@NotNull String name) {
        return Optional.empty();
    }
}
