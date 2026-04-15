package me.whereareiam.socialismus.model.requirement;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;

/**
 * Represents a requirement condition that can be checked against a specific value.
 * This class is used to define validation rules or access conditions within the plugin.
 *
 * <p>A requirement consists of two main components:</p>
 * <ul>
 *   <li>A condition type that determines how the comparison should be performed</li>
 *   <li>An expected value that serves as the comparison target</li>
 * </ul>
 *
 * <p><b>Note:</b> Polymorphic type registration is handled programmatically via
 * {@code Config.registerPolymorphic(Requirement.class)} to allow modules to register
 * their own requirement subtypes without modifying this class.</p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Requirement {
	/**
	 * The type of condition to be checked
	 */
	private RequirementConditionType condition;

	/**
	 * The expected value to compare against
	 */
	private String expected;
}