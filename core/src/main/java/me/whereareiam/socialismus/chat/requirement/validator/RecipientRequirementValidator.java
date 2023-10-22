package me.whereareiam.socialismus.chat.requirement.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.requirement.SeePermissionRequirement;
import me.whereareiam.socialismus.chat.requirement.WorldRequirement;

import java.util.Arrays;

@Singleton
public class RecipientRequirementValidator extends RequirementValidator {
    @Inject
    public RecipientRequirementValidator(SeePermissionRequirement seePermissionRequirement,
                                         WorldRequirement worldRequirement) {
        super(Arrays.asList(seePermissionRequirement, worldRequirement));
    }
}
