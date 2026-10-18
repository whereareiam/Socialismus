package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.model.requirement.type.WorldRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.PlatformType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Singleton
public class WorldRequirementValidation implements RequirementValidation {

	@Inject
	public WorldRequirementValidation(
			ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry
	) {
		registry.register(Constants.Requirements.WORLD, this);
	}

	@Override
	public @NotNull RequirementResult evaluate(@NotNull Requirement requirement, @NotNull SocialismusPlayer player) {
		if (!(requirement instanceof WorldRequirement wr)) return RequirementResult.denied();
		// A proxy knows no worlds, so it cannot tell the player anything true about them.
		if (!PlatformType.isGameServer()) return RequirementResult.denied();

		Logger.debug("Checking world requirement for player " + player.getUsername());
		boolean checkResult = false;
		switch (wr.getCondition()) {
			case EQUALS ->
					checkResult = wr.getWorlds().size() == 1 && wr.getWorlds().get(0).equals(player.getLocation());
			case CONTAINS -> checkResult = wr.getWorlds().contains(player.getLocation());
		}

		Logger.debug("World check result " + checkResult + " for player " + player.getUsername());
		String[] expectedValues = wr.getExpected().split("\\|");
		for (String expectedValue : expectedValues)
			if (String.valueOf(checkResult).equals(expectedValue))
				return RequirementResult.passed();

		return RequirementDenials.deny(Constants.Requirements.WORLD, checkResult, Map.of(
				"worlds", RequirementDenials.listed(wr.getWorlds()),
				"world", String.valueOf(player.getLocation())
		));
	}
}
