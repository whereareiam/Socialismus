package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.model.requirement.type.ChatRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.PlatformType;
import org.jetbrains.annotations.NotNull;

@Singleton
public class ChatRequirementValidation implements RequirementValidation {
	@Inject
	public ChatRequirementValidation(
			ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry
	) {
		registry.register(Constants.Requirements.CHAT, this);
	}

	@Override
	public @NotNull RequirementResult evaluate(@NotNull Requirement requirement, @NotNull SocialismusPlayer player) {
		if (!(requirement instanceof ChatRequirement cr)) return RequirementResult.denied();
		if (!PlatformType.isGameServer()) return RequirementResult.denied();

		Chat lastChat = player.getData(Constants.DataKeys.LAST_CHAT);
		String lastChatId = lastChat != null ? lastChat.getId() : "null";

		Logger.debug("Checking chat requirement for player " + player.getUsername() + " [" + lastChatId + "]");
		boolean checkResult = false;
		switch (cr.getCondition()) {
			case EQUALS ->
					checkResult = cr.getChatIdentifiers().size() == 1 && cr.getChatIdentifiers().get(0).equals(lastChatId);
			case CONTAINS ->
					checkResult = cr.getChatIdentifiers().contains(lastChatId);
		}

		String[] expectedValues = cr.getExpected().split("\\|");
		for (String expectedValue : expectedValues) {
			if (String.valueOf(checkResult).equals(expectedValue)) {
				Logger.debug("Found matching expected value: " + checkResult + " for player " + player.getUsername());
				return RequirementResult.passed();
			}
		}

		Logger.debug("No matching expected values found for player " + player.getUsername());
		return RequirementResult.denied();
	}
}
