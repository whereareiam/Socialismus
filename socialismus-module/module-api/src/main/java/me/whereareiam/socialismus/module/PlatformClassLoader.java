package me.whereareiam.socialismus.module;

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
}
