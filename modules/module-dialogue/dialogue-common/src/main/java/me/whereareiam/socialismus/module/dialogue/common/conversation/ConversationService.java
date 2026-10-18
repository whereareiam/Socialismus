package me.whereareiam.socialismus.module.dialogue.common.conversation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.dialogue.api.ConversationStore;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.ConversationKey;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Keeps the conversations of private messages within the limits of {@code messaging.conversations}.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ConversationService {
	private final ConversationStore store;
	private final Provider<DialogueSettings> settings;

	/**
	 * Returns the conversation of two players, or a new one when they have none.
	 */
	public Conversation getOrCreateConversation(String player1, String player2) {
		return store.find(ConversationKey.of(player1, player2))
				.orElseGet(() -> Conversation.builder()
						.id(UUID.randomUUID().toString())
						.participants(Set.of(player1, player2))
						.messageCount(0)
						.build());
	}

	/**
	 * Adds a message to a conversation and saves it, dropping the oldest messages and the least recently active
	 * conversations of its participants beyond the limits.
	 */
	public void addMessage(Conversation conversation, Message message) {
		DialogueSettings.Messaging.Conversations limits = settings.get().getMessaging().getConversations();

		List<Message> messages = conversation.getMessages();
		while (!messages.isEmpty() && messages.size() >= limits.getMaxMessages())
			messages.removeFirst();

		conversation.addMessage(message);
		store.save(conversation, limits.getTtl());

		for (String participant : conversation.getParticipants())
			store.findByParticipant(participant).stream()
					.skip(Math.max(limits.getMaxConversations(), 0))
					.forEach(old -> store.delete(old.key()));
	}

	/**
	 * Returns a player's conversations, the most recently active first.
	 */
	public List<Conversation> getPlayerConversations(String playerName) {
		return store.findByParticipant(playerName);
	}
}
