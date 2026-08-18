package me.whereareiam.socialismus.module.bubbler.common.requirement;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.module.bubbler.api.model.requirement.ActivatorRequirement;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;
import me.whereareiam.socialismus.module.bubbler.common.BubblerConstants;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;

/**
 * Validates activator requirements for bubble messages.
 * This is a Bubbler-specific requirement validation that checks whether
 * a bubble was activated via chat or command.
 */
@Singleton
public class ActivatorRequirementValidation implements RequirementValidation {
	@Inject
	public ActivatorRequirementValidation(
			ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry
	) {
		registry.register(BubblerConstants.Requirements.ACTIVATOR, this);
	}

	@Override
	public boolean check(Requirement requirement, SocialismusPlayer player) {
		if (!(requirement instanceof ActivatorRequirement activatorReq)) {
			Logger.warn("Invalid requirement type for activator validation");
			return false;
		}

		ActivatorType lastActivator = player.getData(BubblerConstants.DataKeys.LAST_ACTIVATOR);
		String lastActivatorName = lastActivator != null ? lastActivator.name() : null;

		Logger.debug("Checking activator requirement for player " + player.getUsername() + " [" + lastActivatorName + "]");

		boolean checkResult = false;
		switch (activatorReq.getCondition()) {
			case EQUALS ->
					checkResult = activatorReq.getActivators().size() == 1 && activatorReq.getActivators().get(0).equals(lastActivatorName);
			case CONTAINS ->
					checkResult = activatorReq.getActivators().contains(lastActivatorName);
		}

		String[] expectedValues = activatorReq.getExpected().split("\\|");
		for (String expectedValue : expectedValues) {
			if (String.valueOf(checkResult).equals(expectedValue)) {
				Logger.debug("Found matching expected value: " + checkResult + " for player " + player.getUsername());
				return true;
			}
		}

		Logger.debug("No matching expected values found for player " + player.getUsername());
		return false;
	}
}
