package me.whereareiam.socialismus.module.mentionator.common.config.defaults;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.model.phrase.Phrases;

/**
 * The phrases Mentionator ships: under {@code denied}, what a sender is told when a tag's {@code requires} refuses
 * them, and the phrases the default tags refer to with {@code <ref:...>}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MentionatorPhrasesDefaults {
	/**
	 * The default phrases.
	 */
	public static final Phrases PHRASES = Phrases.builder()
			.section("mentionator", mentionator -> mentionator
					.section("denied", denied -> denied
							.text("permission.missing", "{prefix}<white>You may not use <gray>@{tag}</gray>.</white>")
							.text("world.outside", "{prefix}<white><gray>@{tag}</gray> only works in <gray>{worlds}</gray>.</white>")
							.text("world.inside", "{prefix}<white><gray>@{tag}</gray> does not work in <gray>{world}</gray>.</white>")
							.text("server.outside", "{prefix}<white><gray>@{tag}</gray> only works on <gray>{servers}</gray>.</white>")
							.text("server.inside", "{prefix}<white><gray>@{tag}</gray> does not work on <gray>{server}</gray>.</white>")
							.text("cooldown.waiting", "{prefix}<white>Wait <gray>{remaining}</gray> before using <gray>@{tag}</gray> again.</white>"))
					// Phrases of the default tags in tags/groups.yml; name your own the same way.
					.text("everyone-denied", "{prefix}<white>Only staff may tag everyone.</white>")
					.text("everyone-cooldown", "{prefix}<white>Everyone was tagged a moment ago; wait <gray>{remaining}</gray>.</white>"))
			.build();
}
