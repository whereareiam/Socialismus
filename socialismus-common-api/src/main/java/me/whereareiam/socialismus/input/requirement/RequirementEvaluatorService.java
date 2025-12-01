package me.whereareiam.socialismus.input.requirement;

import me.whereareiam.socialismus.model.player.DummyPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;

/**
 * Service interface for evaluating requirement groups against players.
 * This service determines if a player meets the specified requirements
 * defined in a requirement group.
 *
 * <p>The service handles various requirement condition types such as HAS,
 * CONTAINS, EQUALS, and numeric comparisons to validate player attributes
 * or permissions.</p>
 */
public interface RequirementEvaluatorService {
    /**
     * Checks if a player meets the requirements specified in a requirement group.
     *
     * @param group the requirement group to evaluate
     * @param dummyPlayer the player to check against the requirements
     * @return true if the player meets all requirements, false otherwise
     */
    boolean check(RequirementGroup group, DummyPlayer dummyPlayer);
}