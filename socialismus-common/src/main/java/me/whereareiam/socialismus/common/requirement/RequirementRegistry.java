package me.whereareiam.socialismus.common.requirement;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.registry.base.ExtendedRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementValidation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for requirement validations.
 * Supports namespaced requirement types, allowing modules to register their own requirements
 * without modifying core code.
 */
@Singleton
public class RequirementRegistry implements ExtendedRegistry<String, RequirementValidation> {
    private final Map<String, RequirementValidation> requirementCheckers = new ConcurrentHashMap<>();

    /**
     * Registers a requirement validation using a type-safe key.
     *
     * @param key the requirement key
     * @param validation the validation implementation
     */
    public void register(@NotNull RequirementKey<?> key, @NotNull RequirementValidation validation) {
        requirementCheckers.put(key.getFullKey(), validation);
    }

    @Override
    public void register(@NotNull String fullKey, @NotNull RequirementValidation validation) {
        requirementCheckers.put(fullKey, validation);
    }

    @Override
    @NotNull
    public Map<String, RequirementValidation> getRegistry() {
        return requirementCheckers;
    }

    @Override
    @Nullable
    public RequirementValidation get(@NotNull String fullKey) {
        return requirementCheckers.get(fullKey);
    }
}
