package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Singleton
public class PermissionRequirementValidation implements RequirementValidation {
	@Inject
	public PermissionRequirementValidation(
			ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry
	) {
		registry.register(Constants.Requirements.PERMISSION, this);
	}

	@Override
	public @NotNull RequirementResult evaluate(@NotNull Requirement requirement, @NotNull SocialismusPlayer player) {
		if (!(requirement instanceof PermissionRequirement pr)) return RequirementResult.denied();

		Logger.debug("Checking permission requirement for player " + player.getUsername());
		boolean checkResult = false;
		switch (pr.getCondition()) {
			case HAS -> checkResult = pr.getPermissions().stream()
					.allMatch(player::hasPermission);
			case CONTAINS -> checkResult = pr.getPermissions().stream()
					.anyMatch(player::hasPermission);
		}

		Logger.debug("Permission check result " + checkResult + " for " + player.getUsername());
		if (String.valueOf(checkResult).equals(pr.getExpected())) return RequirementResult.passed();

		return RequirementDenials.deny(Constants.Requirements.PERMISSION, checkResult, Map.of("permissions", RequirementDenials.listed(pr.getPermissions())));
	}
}
