package me.whereareiam.socialismus.module.dialogue.api;

import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.ConversationKey;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Keeps the conversations of Dialogue. Dialogue keeps them in memory, so they last until the server stops; a store
 * backed by a database keeps them across restarts and, when it is shared, across the servers of a network.
 * <p>
 * A conversation expires once it has not been saved for the time given with its last save; an expired
 * conversation is never returned again.
 */
public interface ConversationStore {
	/**
	 * Returns the conversation of two players.
	 *
	 * @param key the key of its two participants
	 * @return the conversation, or empty when there is none or it expired
	 */
	@NotNull Optional<Conversation> find(@NotNull ConversationKey key);

	/**
	 * Returns the conversations a player takes part in, the most recently active first.
	 *
	 * @param participant the player's name, in any casing
	 * @return the player's conversations that have not expired
	 */
	@NotNull List<Conversation> findByParticipant(@NotNull String participant);

	/**
	 * Saves a new conversation or the change of an existing one, replacing what was saved under its key.
	 *
	 * @param conversation the conversation
	 * @param ttl          how long the conversation lasts from now unless it is saved again
	 */
	void save(@NotNull Conversation conversation, @NotNull Duration ttl);

	/**
	 * Removes a conversation; nothing happens when there is none.
	 *
	 * @param key the key of its two participants
	 */
	void delete(@NotNull ConversationKey key);
}
