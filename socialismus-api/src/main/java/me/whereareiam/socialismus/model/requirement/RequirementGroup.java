package me.whereareiam.socialismus.model.requirement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a group of requirements that can be evaluated together using a logical operator.
 * This class allows for complex requirement combinations to be defined and evaluated.
 *
 * <p>Each requirement group consists of:</p>
 * <ul>
 *   <li>A logical operator (AND, OR) that determines how multiple requirements are combined</li>
 *   <li>A map of requirements, organized by their namespaced type key (e.g., "socialismus:chat")</li>
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
     * Map of requirements organized by their string keys (e.g., "socialismus:permission").
     * Use the builder's type-safe methods to add requirements with RequirementKey instances.
     */
    private Map<String, ? extends Requirement> groups;

    /**
     * Type-safe helper to create a single-entry requirement map.
     * Use this with the builder: {@code RequirementGroup.builder().groups(of(key, requirement)).build()}
     *
     * @param key the requirement key
     * @param requirement the requirement
     * @param <T> the requirement type
     * @return a map with the requirement entry
     */
    public static <T extends Requirement> Map<String, T> of(RequirementKey<T> key, T requirement) {
        Map<String, T> map = new HashMap<>();
        map.put(key.getFullKey(), requirement);
        return map;
    }
}