package me.whereareiam.socialismus.api.type.requirement;

/**
 * Defines logical operators used to combine multiple requirements in a requirement group.
 * These operators determine how multiple conditions are evaluated together.
 *
 * <p>The following operators are supported:</p>
 * <ul>
 *   <li>{@link #AND}: All conditions must be true</li>
 *   <li>{@link #OR}: At least one condition must be true</li>
 *   <li>{@link #XOR}: Exactly one condition must be true</li>
 *   <li>{@link #NOT}: Inverts the condition result</li>
 *   <li>{@link #NAND}: Not all conditions can be true</li>
 *   <li>{@link #NOR}: All conditions must be false</li>
 * </ul>
 */
public enum RequirementOperatorType {
    /** Requires all conditions to be met. Fails if any condition fails. */
    AND,

    /** Passes if any condition is met. Fails only if all conditions fail. */
    OR,

    /** Passes if exactly one condition is met. Fails if zero or multiple conditions are met. */
    XOR,

    /** Inverts the result of the condition check. */
    NOT,

    /** Passes if not all conditions are met. Inverse of AND operator. */
    NAND,

    /** Passes if no conditions are met. Inverse of OR operator. */
    NOR
}