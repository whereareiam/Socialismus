package me.whereareiam.socialismus.input.registry;

import java.util.Map;

/**
 * Generic registry interface for mapping two types of objects.
 * Used for registering and managing pairs of related objects in the Socialismus plugin.
 *
 * @param <A> the type of the key objects
 * @param <B> the type of the value objects
 */
public interface ExtendedRegistry<A, B> {
    /**
     * Registers a pair of objects in the registry.
     *
     * @param a the key object
     * @param b the value object
     */
    void register(A a, B b);

    /**
     * Returns all registered pairs as a map.
     *
     * @return map containing all registered key-value pairs
     */
    Map<A, B> getRegistry();

    /**
     * Retrieves a value object by its associated key.
     *
     * @param a the key object
     * @return the associated value object
     */
    B get(A a);
}