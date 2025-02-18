package me.whereareiam.socialismus.api.type.requirement;

/**
 * Defines the types of requirements that can be used in requirement groups.
 * Each type represents a different category of checks that can be performed.
 *
 * <p>Available requirement types:</p>
 * <ul>
 *   <li>{@link #PLACEHOLDER}: Requirements based on placeholder values</li>
 *   <li>{@link #PERMISSION}: Requirements checking player permissions</li>
 *   <li>{@link #SERVER}: Requirements related to server conditions</li>
 *   <li>{@link #WORLD}: Requirements specific to in-game worlds</li>
 *   <li>{@link #CHAT}: Requirements related to chat conditions</li>
 * </ul>
 */
public enum RequirementType {
    /** Checks placeholder-based conditions */
    PLACEHOLDER,

    /** Checks permission-based conditions */
    PERMISSION,

    /** Checks server-related conditions */
    SERVER,

    /** Checks world-specific conditions */
    WORLD,

    /** Checks chat-related conditions */
    CHAT
}