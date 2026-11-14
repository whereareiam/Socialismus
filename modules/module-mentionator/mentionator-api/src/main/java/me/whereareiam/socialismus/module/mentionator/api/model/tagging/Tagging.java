package me.whereareiam.socialismus.module.mentionator.api.model.tagging;

import me.whereareiam.socialismus.model.attribute.AttributeKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * The tags a chat message holds, in the order they are written, carried by the message as its {@link #ATTRIBUTE}
 * from the server it was written on to every server that shows it.
 *
 * <pre>{@code
 * boolean tagged = message.getAttribute(Tagging.ATTRIBUTE).map(tagging -> tagging.targets(player.getUniqueId())).orElse(false);
 * }</pre>
 *
 * @param uses the tags, in the order they are written
 */
public record Tagging(@NotNull List<TagUse> uses) {
	/**
	 * The tagging a chat message carries.
	 */
	public static final AttributeKey<Tagging> ATTRIBUTE = AttributeKey.create("mentionator", "tagging", Tagging.class);

	public Tagging {
		uses = List.copyOf(uses);
	}

	/**
	 * Returns whether a tag of the message tags a player.
	 *
	 * @param player the player's unique id
	 * @return whether they are tagged
	 */
	public boolean targets(@NotNull UUID player) {
		return uses.stream().anyMatch(use -> use.targets().contains(player));
	}

	/**
	 * Returns the tagging of one tag of the message alone, to tell who that tag tags.
	 *
	 * @param use the tag
	 * @return its tagging
	 */
	public @NotNull Tagging only(@NotNull TagUse use) {
		return new Tagging(List.of(use));
	}
}
