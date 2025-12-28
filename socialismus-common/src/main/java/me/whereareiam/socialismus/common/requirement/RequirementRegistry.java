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
public class RequirementRegistry implements ExtendedRegistry<RequirementKey<?>, RequirementValidation> {
    private final Map<String, RequirementValidation> requirementCheckers = new ConcurrentHashMap<>();

    @Override
    public void register(@NotNull RequirementKey<?> key, @NotNull RequirementValidation validation) {
        requirementCheckers.put(key.getFullKey(), validation);
    }

    @Override
    @NotNull
    public Map<RequirementKey<?>, RequirementValidation> getRegistry() {
        // This method is not commonly used, but we need to maintain interface compatibility
        // Converting the internal String-keyed map to RequirementKey map would require
        // reconstructing RequirementKey objects, which isn't ideal
        throw new UnsupportedOperationException(
            "getRegistry() is not supported. Use get(String) or get(RequirementKey) instead."
        );
    }

    @Override
    @Nullable
    public RequirementValidation get(@NotNull RequirementKey<?> key) {
        return requirementCheckers.get(key.getFullKey());
    }

    /**
     * Gets a requirement validation by its string key.
     * This is used when deserializing requirements from config files.
     *
     * @param fullKey the full namespaced key (e.g., "socialismus:permission") or short key (e.g., "PERMISSION")
     * @return the validation implementation, or null if not registered
     */
    @Nullable
    public RequirementValidation get(@NotNull String fullKey) {
        // First try exact match
        RequirementValidation validation = requirementCheckers.get(fullKey);
        if (validation != null) {
            return validation;
        }
        
        // If no namespace present, search through all registered keys
        if (!fullKey.contains(":")) {
            String normalizedKey = fullKey.toLowerCase();
            for (Map.Entry<String, RequirementValidation> entry : requirementCheckers.entrySet()) {
                String registeredKey = entry.getKey();
                // Extract the type part after the colon
                if (registeredKey.contains(":")) {
                    String type = registeredKey.substring(registeredKey.indexOf(':') + 1);
                    if (type.equals(normalizedKey)) {
                        return entry.getValue();
                    }
                }
            }
        }
        
        return null;
    }
}
