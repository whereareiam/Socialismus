package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.type.MessageRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.PlatformType;

@Singleton
public class MessageRequirementValidation implements RequirementValidation {
	@Inject
	public MessageRequirementValidation(
			ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry
	) {
		registry.register(Constants.Requirements.MESSAGE, this);
	}

	@Override
	public boolean check(Requirement requirement, SocialismusPlayer player) {
		if (!(requirement instanceof MessageRequirement mr)) return false;
		if (!PlatformType.isGameServer()) return false;

		String lastMessage = player.getData(Constants.DataKeys.LAST_MESSAGE);
		
		Logger.debug("Checking message requirement for player " + player.getUsername() + " [message: " + (lastMessage != null ? "\"" + lastMessage + "\"" : "null") + "]");
		boolean checkResult = false;
		switch (mr.getCondition()) {
			case EQUALS -> {
				if (lastMessage != null && mr.getMessages() != null && mr.getMessages().size() == 1) {
					checkResult = lastMessage.equals(mr.getMessages().get(0));
				}
			}
			case CONTAINS -> {
				if (lastMessage != null && mr.getMessages() != null) {
					for (String messagePattern : mr.getMessages()) {
						if (lastMessage.contains(messagePattern)) {
							checkResult = true;
							break;
						}
					}
				}
			}
			case HAS ->
					checkResult = lastMessage != null && !lastMessage.isEmpty();
		}

		String[] expectedValues = mr.getExpected().split("\\|");
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
