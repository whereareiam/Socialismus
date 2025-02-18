package me.whereareiam.socialismus.api.type.requirement;

/**
 * Enumerates the different types of conditions that can be used in requirements.
 * These condition types define how values should be compared or validated.
 *
 * <p>The conditions support various comparison operations such as:</p>
 * <ul>
 *   <li>Existence checking (HAS)</li>
 *   <li>Substring matching (CONTAINS)</li>
 *   <li>Equality comparison (EQUALS)</li>
 *   <li>Numeric comparisons (GREATER_THAN, LESS_THAN, etc.)</li>
 * </ul>
 */
public enum RequirementConditionType {
    /** Checks if a value exists or is present */
    HAS,

    /** Checks if one string contains another */
    CONTAINS,

    /** Checks if two values are exactly equal */
    EQUALS,

    /** Checks if a numeric value is greater than another */
    GREATER_THAN,

    /** Checks if a numeric value is less than another */
    LESS_THAN,

    /** Checks if a numeric value is greater than or equal to another */
    GREATER_THAN_OR_EQUALS,

    /** Checks if a numeric value is less than or equal to another */
    LESS_THAN_OR_EQUALS
}