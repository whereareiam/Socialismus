package me.whereareiam.socialismus.api.input;

import com.alessiodp.libby.Library;

/**
 * Interface for managing and resolving runtime dependencies in the Socialismus plugin.
 * Provides functionality to handle library dependencies using the Libby library system,
 * allowing dynamic loading and management of external dependencies.
 */
public interface DependencyResolver {
    /**
     * Resolves all registered dependencies, preparing them for loading.
     */
    void resolveDependencies();

    /**
     * Loads all resolved libraries into the runtime.
     */
    void loadLibraries();

    /**
     * Adds a new dependency to be resolved and loaded.
     *
     * @param library the library dependency to add
     */
    void addDependency(Library library);

    /**
     * Removes all registered dependencies.
     */
    void clearDependencies();
}