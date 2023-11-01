package me.whereareiam.socialismus.module.bubblechat.requirement.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.bubblechat.requirement.SeePermissionRequirement;

import java.util.Arrays;

@Singleton
public class RecipientRequirementValidator extends RequirementValidator {
    @Inject
    public RecipientRequirementValidator(SeePermissionRequirement seePermissionRequirement) {
        super(Arrays.asList(seePermissionRequirement));
    }
}
