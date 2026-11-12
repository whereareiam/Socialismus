package me.whereareiam.socialismus.module.bubbler.api.model.requirement;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.conditia.model.Part;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

/**
 * The part Bubbler's places supply beside the players of {@code RequirementParts}: what raised the bubble, which
 * {@code bubbler:activator} and {@code bubbler:chat} are about.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BubblerParts {
	/**
	 * The bubble message being shown: its activator and, when a chat message raised it, that message's chat.
	 */
	public static final Part<BubbleMessage> ACTIVATION = Part.of("activation", BubbleMessage.class);
}
