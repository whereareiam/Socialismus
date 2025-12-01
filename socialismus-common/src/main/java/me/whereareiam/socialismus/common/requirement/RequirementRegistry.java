package me.whereareiam.socialismus.common.requirement;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.input.registry.ExtendedRegistry;
import me.whereareiam.socialismus.input.requirement.RequirementValidation;
import me.whereareiam.socialismus.type.requirement.RequirementType;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class RequirementRegistry implements ExtendedRegistry<RequirementType, RequirementValidation> {
    private final Map<RequirementType, RequirementValidation> requirementCheckers = new HashMap<>();

    @Override
    public void register(RequirementType requirementType, RequirementValidation requirementValidation) {
        requirementCheckers.put(requirementType, requirementValidation);
    }

    @Override
    public Map<RequirementType, RequirementValidation> getRegistry() {
        return requirementCheckers;
    }

    @Override
    public RequirementValidation get(RequirementType requirementType) {
        return requirementCheckers.get(requirementType);
    }
}
