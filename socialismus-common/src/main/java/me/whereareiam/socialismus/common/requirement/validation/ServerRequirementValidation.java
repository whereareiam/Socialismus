package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.input.registry.ExtendedRegistry;
import me.whereareiam.socialismus.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.model.player.DummyPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.type.ServerRequirement;
import me.whereareiam.socialismus.type.requirement.RequirementType;

@Singleton
public class ServerRequirementValidation implements RequirementValidation {

	@Inject
	public ServerRequirementValidation(
			ExtendedRegistry<RequirementType, RequirementValidation> registry
	) {
		registry.register(RequirementType.SERVER, this);
	}

	@Override
	public boolean check(Requirement requirement, DummyPlayer dummyPlayer) {
		if (!(requirement instanceof ServerRequirement sr)) return false;

		String server = dummyPlayer.getServer();
		if (server == null)
			return false;

		Logger.debug("Checking server requirement for player {}", dummyPlayer.getUsername());
		boolean checkResult = false;
		switch (sr.getCondition()) {
			case EQUALS -> checkResult = sr.getServers().size() == 1 && sr.getServers().get(0).equals(server);
			case CONTAINS -> checkResult = sr.getServers().contains(server);
		}

		String[] expectedValues = sr.getExpected().split("\\|");
		for (String expectedValue : expectedValues)
			if (String.valueOf(checkResult).equals(expectedValue)) {
				Logger.debug("Server check result {} for player {}", checkResult, dummyPlayer.getUsername());
				return true;
			}

		return false;
	}
}
