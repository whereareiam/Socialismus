package me.whereareiam.socialismus.module.mentionator.common.value;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A player's values of the placeholders tags name, such as {@code %luckperms_primary_group_name%}.
 *
 * @param values the values by placeholder
 */
public record Values(@NotNull Map<String, String> values) {
	public Values {
		values = Map.copyOf(values);
	}
}
