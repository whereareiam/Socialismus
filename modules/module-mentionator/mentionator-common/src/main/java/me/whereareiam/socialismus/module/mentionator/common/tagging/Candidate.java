package me.whereareiam.socialismus.module.mentionator.common.tagging;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A player a tag may tag, with the values of the placeholders tags name, as their own server read them.
 *
 * @param player the player
 * @param values their values by placeholder, such as {@code %luckperms_primary_group_name%}
 */
public record Candidate(@NotNull SocialismusPlayer player, @NotNull Map<String, String> values) {
	public Candidate {
		values = Map.copyOf(values);
	}
}
