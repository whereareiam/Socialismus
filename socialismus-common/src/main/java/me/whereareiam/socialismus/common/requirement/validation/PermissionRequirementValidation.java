package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.requirement.RequirementType;

@Singleton
public class PermissionRequirementValidation implements RequirementValidation {
	@Inject
	public PermissionRequirementValidation(
			ExtendedRegistry<RequirementType, RequirementValidation> registry
	) {
		registry.register(RequirementType.PERMISSION, this);
	}

	@Override
	public boolean check(Requirement requirement, SocialismusPlayer player) {
		if (!(requirement instanceof PermissionRequirement pr)) return false;

		Logger.debug("Checking permission requirement for player " + player.getUsername());
		boolean checkResult = false;
		switch (pr.getCondition()) {
			case HAS -> checkResult = pr.getPermissions().stream()
					.allMatch(player::hasPermission);
			case CONTAINS -> checkResult = pr.getPermissions().stream()
					.anyMatch(player::hasPermission);
		}

		Logger.debug("Permission check result " + checkResult + " for " + player.getUsername());

		return String.valueOf(checkResult).equals(pr.getExpected());
	}
}
