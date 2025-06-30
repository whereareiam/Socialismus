package me.whereareiam.socialismus.api.model.chat.message;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import net.kyori.adventure.text.Component;

import java.util.Set;

/**
 * Represents a chat message in the Socialismus chat system.
 * This class holds all information about a chat message, including its sender,
 * recipients, content, and various message states.
 *
 * <p>The class uses Lombok's {@code @SuperBuilder} pattern for flexible object creation</p>
 */
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatMessage {
	/**
	 * Unique identifier for the message.
	 */
	private final int id;

	/**
	 * The player who sent the message.
	 */
	private final DummyPlayer sender;

	/**
	 * Set of players who should receive this message.
	 */
	private Set<DummyPlayer> recipients;

	/**
	 * The actual content of the message as a Kyori Adventure Component.
	 * Marked as transient as Components aren't serializable directly.
	 */
	private Component content;

	/**
	 * The chat channel this message belongs to.
	 */
	private Chat chat;

	/**
	 * Whether the message has been cancelled and should not be processed.
	 */
	private boolean cancelled;

	/**
	 * Whether the message should be sent through Minecraft's vanilla chat system.
	 */
	private boolean vanillaSending;

	/**
	 * Indicates if the message was sent from a remote source (e.g., another server).
	 */
	private String origin;
}

