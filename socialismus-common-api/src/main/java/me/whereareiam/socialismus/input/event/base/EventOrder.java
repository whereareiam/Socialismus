package me.whereareiam.socialismus.input.event.base;

/**
 * Defines the execution order of event handlers in the Socialismus plugin.
 * Event handlers are processed in ascending order from LOWEST to HIGHEST.
 * This ordering system ensures predictable event processing and allows
 * handlers to be prioritized based on their requirements.
 *
 * The priority levels are:
 * <ul>
 *   <li>{@link #LOWEST} - Executes first, suitable for monitoring</li>
 *   <li>{@link #LOW} - Executes early, for general preprocessing</li>
 *   <li>{@link #NORMAL} - Default priority for most handlers</li>
 *   <li>{@link #HIGH} - Executes late, for important processing</li>
 *   <li>{@link #HIGHEST} - Executes last, for critical operations</li>
 * </ul>
 */
public enum EventOrder {
    /**
     * Lowest priority, executed first
     */
    LOWEST,

    /**
     * Low priority, executed after LOWEST
     */
    LOW,

    /**
     * Normal priority, default execution order
     */
    NORMAL,

    /**
     * High priority, executed before HIGHEST
     */
    HIGH,

    /**
     * Highest priority, executed last
     */
    HIGHEST
}