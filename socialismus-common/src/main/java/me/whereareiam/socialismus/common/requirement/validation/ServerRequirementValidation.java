package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.PlatformType;
import me.whereareiam.socialismus.api.input.registry.ExtendedRegistry;
import me.whereareiam.socialismus.api.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.requirement.Requirement;
import me.whereareiam.socialismus.api.model.requirement.type.ServerRequirement;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.type.requirement.RequirementType;

@Singleton
public class ServerRequirementValidation implements RequirementValidation {
    private final LoggingHelper loggingHelper;

    @Inject
    public ServerRequirementValidation(ExtendedRegistry<RequirementType, RequirementValidation> registry, LoggingHelper loggingHelper) {
	    this.loggingHelper = loggingHelper;

	    registry.register(RequirementType.SERVER, this);
    }

    @Override
    public boolean check(Requirement requirement, DummyPlayer dummyPlayer) {
        if (!(requirement instanceof ServerRequirement sr)) return false;
        if (!PlatformType.isProxy()) return false;

        loggingHelper.debug("Checking server requirement for player {}", dummyPlayer.getUsername());
        boolean checkResult = false;
        switch (sr.getCondition()) {
            case EQUALS ->
                    checkResult = sr.getServers().size() == 1 && sr.getServers().getFirst().equals(dummyPlayer.getLocation());
            case CONTAINS -> checkResult = sr.getServers().contains(dummyPlayer.getLocation());
        }

        String[] expectedValues = sr.getExpected().split("\\|");
        for (String expectedValue : expectedValues)
            if (String.valueOf(checkResult).equals(expectedValue)) {
                loggingHelper.debug("Server check result {} for player {}", checkResult, dummyPlayer.getUsername());
                return true;
            }

        loggingHelper.debug("Server check result {} for player {}", checkResult, dummyPlayer.getUsername());

        return false;
    }
}
