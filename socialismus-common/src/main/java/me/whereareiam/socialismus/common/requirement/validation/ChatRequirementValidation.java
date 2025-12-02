package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.type.ChatRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.requirement.RequirementType;

@Singleton
public class ChatRequirementValidation implements RequirementValidation {
	@Inject
	public ChatRequirementValidation(
			ExtendedRegistry<RequirementType, RequirementValidation> registry
	) {
		registry.register(RequirementType.CHAT, this);
	}

	@Override
	public boolean check(Requirement requirement, SocialismusPlayer player) {
		if (!(requirement instanceof ChatRequirement cr)) return false;
		if (!PlatformType.isGameServer()) return false;

		Logger.debug("Checking chat requirement for player " + player.getUsername());
		boolean checkResult = false;
		switch (cr.getCondition()) {
			case EQUALS ->
					checkResult = cr.getChatIdentifiers().size() == 1 && cr.getChatIdentifiers().get(0).equals(player.getLastChat() != null ? player.getLastChat().getId() : "null");
			case CONTAINS ->
					checkResult = cr.getChatIdentifiers().contains(player.getLastChat() != null ? player.getLastChat().getId() : "null");
		}

		String[] expectedValues = cr.getExpected().split("\\|");
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
