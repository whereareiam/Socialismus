package me.whereareiam.socialismus.common.requirement.validation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.registry.ExtendedRegistry;
import me.whereareiam.socialismus.api.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.requirement.Requirement;
import me.whereareiam.socialismus.api.model.requirement.type.PermissionRequirement;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.type.requirement.RequirementType;

@Singleton
public class PermissionRequirementValidation implements RequirementValidation {
    private final PlatformInteractor interactor;
    private final LoggingHelper loggingHelper;

    @Inject
    public PermissionRequirementValidation(ExtendedRegistry<RequirementType, RequirementValidation> registry, PlatformInteractor interactor, LoggingHelper loggingHelper) {
        this.interactor = interactor;
	    this.loggingHelper = loggingHelper;

	    registry.register(RequirementType.PERMISSION, this);
    }

    @Override
    public boolean check(Requirement requirement, DummyPlayer dummyPlayer) {
        if (!(requirement instanceof PermissionRequirement pr)) return false;

        boolean checkResult = false;
        switch (pr.getCondition()) {
            case HAS -> checkResult = pr.getPermissions().stream()
                    .allMatch(perm -> interactor.hasPermission(dummyPlayer, perm));
            case CONTAINS -> checkResult = pr.getPermissions().stream()
                    .anyMatch(perm -> interactor.hasPermission(dummyPlayer, perm));
        }

        loggingHelper.debug("Permission check result " + checkResult + " for " + dummyPlayer.getUsername());

        return String.valueOf(checkResult).equals(pr.getExpected());
    }
}
