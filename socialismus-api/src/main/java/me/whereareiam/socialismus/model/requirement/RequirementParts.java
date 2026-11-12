package me.whereareiam.socialismus.model.requirement;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.conditia.model.Part;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;

/**
 * The parts Socialismus's places supply to their requirements, which conditions in a {@code requires:} list are
 * about. A condition about a player is about the place's first part unless a block names another:
 *
 * <pre>{@code
 * reaches:                     # a chat's recipients: recipient, sender, message
 *   - permission: socialismus.chat.local   # the recipient's
 *   - sender:
 *       - world: world_nether              # the sender's
 * }</pre>
 *
 * A module's place uses these parts where they mean the same, and declares parts of its own for what only it knows,
 * such as the activator of a bubble.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequirementParts {
	/**
	 * The player who sends a message or does what the place allows.
	 */
	public static final Part<SocialismusPlayer> SENDER = Part.of("sender", SocialismusPlayer.class);

	/**
	 * A player a message is delivered to.
	 */
	public static final Part<SocialismusPlayer> RECIPIENT = Part.of("recipient", SocialismusPlayer.class);

	/**
	 * A player who sees something rendered for them, such as the clear button of a message in chat history.
	 */
	public static final Part<SocialismusPlayer> VIEWER = Part.of("viewer", SocialismusPlayer.class);

	/**
	 * The chat message being sent now: its content, its trigger and, once one is chosen, its chat.
	 */
	public static final Part<ChatMessage> MESSAGE = Part.of("message", ChatMessage.class);
}
