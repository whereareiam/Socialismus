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
     * Finds an enabled platform component that a module may use as an external
     * API dependency.
     *
     * <p>Platforms that do not support dynamic plugin dependencies return an
     * empty result.</p>
     *
     * @param name the platform component name declared by the module
     * @return the component metadata and its class loader, if available
     */
    default @NotNull Optional<PlatformDependency> findDependency(@NotNull String name) {
        return Optional.empty();
    }
}
