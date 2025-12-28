package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.ChatTrigger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.type.TriggerRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.chat.TriggerType;

@Singleton
public class TriggerRequirementValidation implements RequirementValidation {
	@Inject
	public TriggerRequirementValidation(
			ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry
	) {
		registry.register(Constants.Requirements.TRIGGER, this);
	}

	@Override
	public boolean check(Requirement requirement, SocialismusPlayer player) {
		if (!(requirement instanceof TriggerRequirement tr)) return false;
		if (!PlatformType.isGameServer()) return false;

		ChatTrigger lastTrigger = player.getData(Constants.DataKeys.LAST_TRIGGER);
		TriggerType lastTriggerType = lastTrigger != null ? lastTrigger.getType() : null;

		Logger.debug("Checking trigger requirement for player " + player.getUsername() + " [" + lastTriggerType + "]");
		boolean checkResult = false;
		switch (tr.getCondition()) {
			case EQUALS ->
					checkResult = tr.getTriggers().size() == 1 && tr.getTriggers().get(0).equals(lastTriggerType);
			case CONTAINS ->
					checkResult = tr.getTriggers().contains(lastTriggerType);
		}

		String[] expectedValues = tr.getExpected().split("\\|");
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
