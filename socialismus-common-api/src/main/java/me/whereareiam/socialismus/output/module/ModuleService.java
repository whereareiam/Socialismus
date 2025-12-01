package me.whereareiam.socialismus.output.module;

import me.whereareiam.socialismus.model.module.InternalModule;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing modules in the Socialismus plugin system.
 * Provides methods for loading, unloading, and retrieving module information.
 *
 * <p>This service handles the lifecycle of plugin modules and provides
 * access to module instances at runtime.</p>
 */
public interface ModuleService {
    /**
     * Loads all available modules into the plugin system
     */
    void loadModules();

    /**
     * Unloads all currently active modules from the system
     */
    void unloadModules();

    /**
     * Reloads all modules by unloading and loading them again
     */
    void reloadModules();

    /**
     * Returns a list of all loaded internal modules
     *
     * @return List of loaded {@link InternalModule} instances
     */
    List<InternalModule> getModules();

    /**
     * Retrieves a specific module by its name
     *
     * @param name The name of the module to retrieve
     * @return Optional containing the module if found, empty otherwise
     */
    Optional<InternalModule> getModule(String name);
}