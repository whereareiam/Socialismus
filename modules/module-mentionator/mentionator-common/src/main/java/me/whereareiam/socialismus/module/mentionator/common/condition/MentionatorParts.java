package me.whereareiam.socialismus.module.mentionator.common.condition;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.conditia.model.Part;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

/**
 * The part Mentionator's places add to Socialismus's: the player a tag may tag.
 *
 * <pre>{@code
 * targets:                       # target, sender, message
 *   - sees: [target, sender]
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MentionatorParts {
	/**
	 * A player a tag may tag, or a tagged player who gets a notification.
	 */
	public static final Part<SocialismusPlayer> TARGET = Part.of("target", SocialismusPlayer.class);
}
