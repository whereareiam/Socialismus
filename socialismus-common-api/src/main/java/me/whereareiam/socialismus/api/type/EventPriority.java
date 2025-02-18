package me.whereareiam.socialismus.api.type;

/**
 * Platform-independent event priority enum for the Socialismus plugin.
 * This enum provides a unified priority system that can be mapped to
 * different platform-specific event priorities (e.g., Bukkit, Velocity).
 *
 * The priorities are processed in ascending order:
 * <ul>
 *   <li>{@link #LOWEST} - First to execute</li>
 *   <li>{@link #LOW} - Early execution</li>
 *   <li>{@link #NORMAL} - Standard priority</li>
 *   <li>{@link #HIGH} - Late execution</li>
 *   <li>{@link #HIGHEST} - Last to execute</li>
 * </ul>
 *
 * This enum is used by the dynamic listener system to provide consistent
 * event handling behavior across different platforms while allowing
 * platform-specific priority mapping internally.
 */
public enum EventPriority {
    /**
     * Lowest priority, maps to platform's lowest priority
     */
    LOWEST,

    /**
     * Low priority, maps to platform's low priority
     */
    LOW,

    /**
     * Normal priority, maps to platform's default priority
     */
    NORMAL,

    /**
     * High priority, maps to platform's high priority
     */
    HIGH,

    /**
     * Highest priority, maps to platform's highest priority
     */
    HIGHEST
}