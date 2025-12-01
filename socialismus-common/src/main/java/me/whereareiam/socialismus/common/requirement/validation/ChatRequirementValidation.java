package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.input.registry.ExtendedRegistry;
import me.whereareiam.socialismus.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.model.player.DummyPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.type.ChatRequirement;
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
	public boolean check(Requirement requirement, DummyPlayer dummyPlayer) {
		if (!(requirement instanceof ChatRequirement cr)) return false;
		if (!PlatformType.isGameServer()) return false;

		Logger.debug("Checking chat requirement for player " + dummyPlayer.getUsername());
		boolean checkResult = false;
		switch (cr.getCondition()) {
			case EQUALS ->
					checkResult = cr.getChatIdentifiers().size() == 1 && cr.getChatIdentifiers().get(0).equals(dummyPlayer.getLastChat() != null ? dummyPlayer.getLastChat().getId() : "null");
			case CONTAINS ->
					checkResult = cr.getChatIdentifiers().contains(dummyPlayer.getLastChat() != null ? dummyPlayer.getLastChat().getId() : "null");
		}

		String[] expectedValues = cr.getExpected().split("\\|");
		for (String expectedValue : expectedValues) {
			if (String.valueOf(checkResult).equals(expectedValue)) {
				Logger.debug("Found matching expected value: " + checkResult + " for player " + dummyPlayer.getUsername());
				return true;
			}
		}

		Logger.debug("No matching expected values found for player " + dummyPlayer.getUsername());
		return false;
	}
}
