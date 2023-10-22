package me.whereareiam.socialismus.chat.requirement.validator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.requirement.WorldRequirement;
import me.whereareiam.socialismus.chat.requirement.WritePermissionRequirement;

import java.util.Arrays;

@Singleton
public class SenderRequirementValidator extends RequirementValidator {
    @Inject
    public SenderRequirementValidator(WritePermissionRequirement writePermissionRequirement,
                                      WorldRequirement worldRequirement) {
        super(Arrays.asList(writePermissionRequirement, worldRequirement));
    }
}
