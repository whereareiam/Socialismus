package me.whereareiam.socialismus.registry.base;

/**
 * A generic registry interface that provides registration functionality for
 * different types of components in the Socialismus plugin.
 *
 * <p>This interface allows for type-safe registration of various elements
 * such as commands, events, or integrations. The generic type parameter T
 * represents the type of element that can be registered.</p>
 *
 * @param <T> the type of element that can be registered in this registry
 */
public interface Registry<T> {
    /**
     * Registers a new element in this registry.
     *
     * @param integration the element to register
     */
    void register(T integration);

    /**
     * Removes an element registered before, such as a module's when the module stops; an element that was not
     * registered is ignored.
     *
     * @param element the element to remove
     */
    void unregister(T element);
}
