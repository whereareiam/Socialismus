package me.whereareiam.socialismus.module.dialogue.api.model.conversation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The private messages two players exchanged, which {@code /reply} answers. Its participants keep the names as the
 * players spell themselves; its {@link #key()} ignores their casing.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Conversation {
	/**
	 * Unique identifier of the conversation, which its messages carry.
	 */
	private String id;

	/**
	 * The names of the two players.
	 */
	private Set<String> participants;

	/**
	 * The messages kept of the conversation, the oldest first.
	 */
	@Builder.Default
	private List<Message> messages = new ArrayList<>();

	/**
	 * The name of the player who sent the latest message.
	 */
	private String lastSender;

	/**
	 * When the latest message was sent.
	 */
	private Instant lastActivity;

	/**
	 * How many messages were sent in the conversation, including those no longer kept.
	 */
	private int messageCount;

	/**
	 * Returns the key of the two participants.
	 *
	 * @return the key the conversation is kept under
	 */
	public @NotNull ConversationKey key() {
		return ConversationKey.of(participants);
	}

	/**
	 * Returns the participant who is not the given player.
	 *
	 * @param participant the name of one participant, in any casing
	 * @return the name of the other participant as they spell themselves
	 */
	public @NotNull String partnerOf(@NotNull String participant) {
		return participants.stream()
				.filter(name -> !name.equalsIgnoreCase(participant))
				.findFirst()
				.orElse(participant);
	}

	/**
	 * Adds a message as the latest of the conversation.
	 *
	 * @param message the message, which takes the conversation's id
	 */
	public void addMessage(@NotNull Message message) {
		this.messages.add(message);
		this.lastSender = message.getSender().getUsername();
		this.lastActivity = message.getTimestamp();
		this.messageCount++;
		message.setConversationId(this.id);
	}
}
