package me.whereareiam.socialismus.model.requirement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Type-safe key for requirement types with namespace support.
 * Allows modules to define their own requirement types without modifying core code.
 *
 * <p>Example keys:</p>
 * <ul>
 *   <li>socialismus:chat - Chat requirement (core)</li>
 *   <li>socialismus:trigger - Trigger requirement (core)</li>
 *   <li>bubbler:activator - Activator requirement (Bubbler module)</li>
 * </ul>
 *
 * @param <T> the requirement class type
 */
@Getter
@EqualsAndHashCode
public final class RequirementKey<T extends Requirement> {
	private final String namespace;
	private final String type;
	private final Class<T> requirementClass;

	private RequirementKey(@NotNull String namespace, @NotNull String type, @NotNull Class<T> requirementClass) {
		this.namespace = namespace;
		this.type = type;
		this.requirementClass = requirementClass;
	}

	/**
	 * Creates a new requirement key.
	 *
	 * @param namespace the namespace (typically module name)
	 * @param type the requirement type name
	 * @param requirementClass the requirement class
	 * @param <T> the requirement type
	 * @return a new RequirementKey instance
	 */
	@NotNull
	public static <T extends Requirement> RequirementKey<T> create(
			@NotNull String namespace,
			@NotNull String type,
			@NotNull Class<T> requirementClass
	) {
		return new RequirementKey<>(namespace, type, requirementClass);
	}

	/**
	 * Gets the full namespaced key for config serialization.
	 *
	 * @return the full key in format "namespace:type"
	 */
	@NotNull
	public String getFullKey() {
		return namespace + ":" + type;
	}

	@Override
	public String toString() {
		return getFullKey();
	}
}
