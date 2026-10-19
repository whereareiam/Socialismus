package me.whereareiam.socialismus.model.chat.message;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.attribute.AttributeKey;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatTrigger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
	private final SocialismusPlayer sender;

	/**
	 * Set of players who should receive this message.
	 */
	private Collection<SocialismusPlayer> recipients;

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
	 * The trigger that routed this message into a chat (if any).
	 */
	private ChatTrigger trigger;

	/**
	 * Whether the message has been cancelled and should not be processed.
	 */
	private boolean cancelled;

	/**
	 * Whether the message should be sent through Minecraft's vanilla chat system.
	 */
	private boolean vanillaSending;

	/**
	 * Whether the message was written elsewhere, such as on another server of a network, and reached this server
	 * through {@link me.whereareiam.socialismus.service.chat.ChatCoordinationService#relay}. A relayed message is
	 * only delivered here; it is neither offered to modules as written here nor sent on.
	 */
	private boolean relayed;

	/**
	 * What the message carries for those who know the keys, such as where it came from on a network.
	 */
	@Builder.Default
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Map<AttributeKey<?>, Object> attributes = new ConcurrentHashMap<>();

	/**
	 * Returns an attribute of the message.
	 *
	 * @param key the attribute's key
	 * @param <T> the type of its value
	 * @return the value, or empty when the message does not carry the attribute
	 */
	public <T> @NotNull Optional<T> getAttribute(@NotNull AttributeKey<T> key) {
		return Optional.ofNullable(attributes.get(key)).map(key.getType()::cast);
	}

	/**
	 * Sets an attribute of the message, or removes it.
	 *
	 * @param key   the attribute's key
	 * @param value the value, or {@code null} to remove the attribute
	 * @param <T>   the type of its value
	 */
	public <T> void setAttribute(@NotNull AttributeKey<T> key, @Nullable T value) {
		if (value == null) attributes.remove(key);
		else attributes.put(key, value);
	}

	/**
	 * Returns every attribute the message carries, for copying them onto another message.
	 */
	public @NotNull Map<AttributeKey<?>, Object> getAttributes() {
		return Map.copyOf(attributes);
	}
}

