package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.PlatformType;
import me.whereareiam.socialismus.api.input.registry.ExtendedRegistry;
import me.whereareiam.socialismus.api.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.requirement.Requirement;
import me.whereareiam.socialismus.api.model.requirement.type.ChatRequirement;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.type.requirement.RequirementType;

@Singleton
public class ChatRequirementValidation implements RequirementValidation {
    private final LoggingHelper loggingHelper;

    @Inject
    public ChatRequirementValidation(ExtendedRegistry<RequirementType, RequirementValidation> registry, LoggingHelper loggingHelper) {
	    this.loggingHelper = loggingHelper;

	    registry.register(RequirementType.CHAT, this);
    }

    @Override
    public boolean check(Requirement requirement, DummyPlayer dummyPlayer) {
        if (!(requirement instanceof ChatRequirement cr)) return false;
        if (!PlatformType.isGameServer()) return false;

        loggingHelper.debug("Checking chat requirement for player " + dummyPlayer.getUsername());
        boolean checkResult = false;
        switch (cr.getCondition()) {
            case EQUALS ->
                    checkResult = cr.getChatIdentifiers().size() == 1 && cr.getChatIdentifiers().getFirst().equals(dummyPlayer.getLastChat() != null ? dummyPlayer.getLastChat().getId() : "null");
            case CONTAINS ->
                    checkResult = cr.getChatIdentifiers().contains(dummyPlayer.getLastChat() != null ? dummyPlayer.getLastChat().getId() : "null");
        }

        String[] expectedValues = cr.getExpected().split("\\|");
        for (String expectedValue : expectedValues) {
            if (String.valueOf(checkResult).equals(expectedValue)) {
                loggingHelper.debug("Found matching expected value: " + checkResult + " for player " + dummyPlayer.getUsername());
                return true;
            }
        }

        loggingHelper.debug("No matching expected values found for player " + dummyPlayer.getUsername());
        return false;
    }
}
