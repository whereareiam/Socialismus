package me.whereareiam.socialismus.input.requirement;

import me.whereareiam.socialismus.model.player.DummyPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;

/**
 * Interface for validating individual requirements against a player.
 * Implementations of this interface handle specific requirement types
 * and determine if a player meets those requirements.
 *
 * <p>This interface is used by the requirement system to validate
 * individual requirements within a {@link RequirementGroup}.</p>
 */
public interface RequirementValidation {
    /**
     * Validates if a player meets a specific requirement.
     *
     * @param requirement the requirement to check
     * @param dummyPlayer the player to validate against the requirement
     * @return true if the player meets the requirement, false otherwise
     */
    boolean check(Requirement requirement, DummyPlayer dummyPlayer);
}