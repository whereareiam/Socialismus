package me.whereareiam.socialismus.api.model.chat.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.util.ComponentUtil;
import net.kyori.adventure.text.Component;

import java.io.*;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;

/**
 * Represents a chat message in the Socialismus chat system.
 * This class holds all information about a chat message, including its sender,
 * recipients, content, and various message states. It supports serialization
 * for message persistence and network transfer.
 *
 * <p>The class uses Lombok's {@code @SuperBuilder} pattern for flexible object creation
 * and implements {@link Serializable} for message persistence.</p>
 */
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
public class ChatMessage implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

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
	private transient Component content;

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
	 * Serializes a ChatMessage instance to a Base64 encoded string.
	 * Recipients are cleared during serialization to avoid excessive data.
	 *
	 * @param chatMessage the message to serialize
	 * @return Base64 encoded string representation of the message
	 * @throws IOException if serialization fails
	 */
	public static String serialize(ChatMessage chatMessage) throws IOException {
		ChatMessage tempChatMessage = chatMessage.toBuilder().build();
		tempChatMessage.setRecipients(Collections.emptySet());

		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
		     ObjectOutputStream out = new ObjectOutputStream(bos)) {
			out.writeObject(tempChatMessage);

			return Base64.getEncoder().encodeToString(bos.toByteArray());
		}
	}

	/**
	 * Deserializes a Base64 encoded string back into a ChatMessage instance.
	 *
	 * @param content Base64 encoded string to deserialize
	 * @return the deserialized ChatMessage instance
	 * @throws IOException            if deserialization fails
	 * @throws ClassNotFoundException if the class structure has changed
	 */
	public static ChatMessage deserialize(String content) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(content));
		     ObjectInputStream in = new ObjectInputStream(bis)) {
			return (ChatMessage) in.readObject();
		}
	}

	/**
	 * Custom serialization method to handle the non-serializable Component field.
	 */
	@Serial
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeUTF(ComponentUtil.toString(content));
	}

	/**
	 * Custom deserialization method to restore the Component field.
	 */
	@Serial
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		content = ComponentUtil.toGson(in.readUTF());
	}
}
