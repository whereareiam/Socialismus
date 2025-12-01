package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.type.WorldRequirement;
import me.whereareiam.socialismus.registry.ExtendedRegistry;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.requirement.RequirementType;

@Singleton
public class WorldRequirementValidation implements RequirementValidation {

	@Inject
	public WorldRequirementValidation(
			ExtendedRegistry<RequirementType, RequirementValidation> registry
	) {
		registry.register(RequirementType.WORLD, this);
	}

	@Override
	public boolean check(Requirement requirement, SocialismusPlayer player) {
		if (!(requirement instanceof WorldRequirement wr)) return false;
		if (!PlatformType.isGameServer()) return false;

		Logger.debug("Checking world requirement for player " + player.getUsername());
		boolean checkResult = false;
		switch (wr.getCondition()) {
			case EQUALS ->
					checkResult = wr.getWorlds().size() == 1 && wr.getWorlds().get(0).equals(player.getLocation());
			case CONTAINS -> checkResult = wr.getWorlds().contains(player.getLocation());
		}

		String[] expectedValues = wr.getExpected().split("\\|");
		for (String expectedValue : expectedValues)
			if (String.valueOf(checkResult).equals(expectedValue)) {
				Logger.debug("World check result " + checkResult + " for player " + player.getUsername());
				return true;
			}

		Logger.debug("World check result " + checkResult + " for player " + player.getUsername());

		return false;
	}
}
