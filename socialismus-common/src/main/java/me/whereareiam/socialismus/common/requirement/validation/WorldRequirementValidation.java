package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.registry.ExtendedRegistry;
import me.whereareiam.socialismus.api.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.requirement.Requirement;
import me.whereareiam.socialismus.api.model.requirement.type.WorldRequirement;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.type.requirement.RequirementType;

@Singleton
public class WorldRequirementValidation implements RequirementValidation {

	@Inject
	public WorldRequirementValidation(
			ExtendedRegistry<RequirementType, RequirementValidation> registry
	) {
		registry.register(RequirementType.WORLD, this);
	}

	@Override
	public boolean check(Requirement requirement, DummyPlayer dummyPlayer) {
		if (!(requirement instanceof WorldRequirement wr)) return false;
		if (!PlatformType.isGameServer()) return false;

		Logger.debug("Checking world requirement for player " + dummyPlayer.getUsername());
		boolean checkResult = false;
		switch (wr.getCondition()) {
			case EQUALS ->
					checkResult = wr.getWorlds().size() == 1 && wr.getWorlds().getFirst().equals(dummyPlayer.getLocation());
			case CONTAINS -> checkResult = wr.getWorlds().contains(dummyPlayer.getLocation());
		}

		String[] expectedValues = wr.getExpected().split("\\|");
		for (String expectedValue : expectedValues)
			if (String.valueOf(checkResult).equals(expectedValue)) {
				Logger.debug("World check result " + checkResult + " for player " + dummyPlayer.getUsername());
				return true;
			}

		Logger.debug("World check result " + checkResult + " for player " + dummyPlayer.getUsername());

		return false;
	}
}
