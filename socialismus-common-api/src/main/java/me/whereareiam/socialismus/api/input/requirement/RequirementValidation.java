package me.whereareiam.socialismus.api.input.requirement;

import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.requirement.Requirement;

/**
 * Interface for validating individual requirements against a player.
 * Implementations of this interface handle specific requirement types
 * and determine if a player meets those requirements.
 *
 * <p>This interface is used by the requirement system to validate
 * individual requirements within a {@link me.whereareiam.socialismus.api.model.requirement.RequirementGroup}.</p>
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