package me.whereareiam.socialismus.api.model.requirement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.type.requirement.RequirementOperatorType;
import me.whereareiam.socialismus.api.type.requirement.RequirementType;

import java.util.Map;

/**
 * Represents a group of requirements that can be evaluated together using a logical operator.
 * This class allows for complex requirement combinations to be defined and evaluated.
 *
 * <p>Each requirement group consists of:</p>
 * <ul>
 *   <li>A logical operator (AND, OR) that determines how multiple requirements are combined</li>
 *   <li>A map of requirements, organized by their type</li>
 * </ul>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class RequirementGroup {
    /**
     * The logical operator to apply when evaluating multiple requirements
     */
    private RequirementOperatorType operator;

    /**
     * Map of requirements organized by their type
     */
    private Map<RequirementType, ? extends Requirement> groups;
}