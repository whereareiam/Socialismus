package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.model.requirement.type.ServerRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Singleton
public class ServerRequirementValidation implements RequirementValidation {

	@Inject
	public ServerRequirementValidation(
			ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry
	) {
		registry.register(Constants.Requirements.SERVER, this);
	}

	@Override
	public @NotNull RequirementResult evaluate(@NotNull Requirement requirement, @NotNull SocialismusPlayer player) {
		if (!(requirement instanceof ServerRequirement sr)) return RequirementResult.denied();

		String server = player.getServer();
		if (server == null) return RequirementResult.denied();

		Logger.debug("Checking server requirement for player " + player.getUsername());
		boolean checkResult = false;
		switch (sr.getCondition()) {
			case EQUALS -> checkResult = sr.getServers().size() == 1 && sr.getServers().get(0).equals(server);
			case CONTAINS -> checkResult = sr.getServers().contains(server);
		}

		Logger.debug("Server check result " + checkResult + " for player " + player.getUsername());
		String[] expectedValues = sr.getExpected().split("\\|");
		for (String expectedValue : expectedValues)
			if (String.valueOf(checkResult).equals(expectedValue))
				return RequirementResult.passed();

		return RequirementDenials.deny(Constants.Requirements.SERVER, checkResult, Map.of(
				"servers", RequirementDenials.listed(sr.getServers()),
				"server", server
		));
	}
}
