package me.whereareiam.socialismus.module.mentionator.api;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.api.type.Mode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * What players decide about tags, and which tags they may use.
 */
public interface MentionatorService {
	/**
	 * Returns which tags notify a player; every tag until they choose.
	 *
	 * @param player the player's unique id
	 * @return their mode
	 */
	@NotNull Mode getMode(@NotNull UUID player);

	/**
	 * Sets which tags notify a player, on every server and, with SocialismusDatabase, across restarts.
	 *
	 * @param player the player's unique id
	 * @param mode   their mode
	 */
	void setMode(@NotNull UUID player, @NotNull Mode mode);

	/**
	 * Returns the tags a player may use now: the enabled ones whose {@code requires} they meet.
	 *
	 * @param player the player
	 * @return the tags, highest priority first
	 */
	@NotNull List<Tag> usableTags(@NotNull SocialismusPlayer player);
}
