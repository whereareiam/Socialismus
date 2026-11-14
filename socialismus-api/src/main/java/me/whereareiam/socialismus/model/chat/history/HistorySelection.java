package me.whereareiam.socialismus.model.chat.history;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * The messages of the chat history that one removal takes away: one message by its id, the oldest few, every message
 * of a player, or every message of a player known only by name, such as one who is offline or on another server.
 */
public sealed interface HistorySelection {
	/**
	 * Selects the message with the given id.
	 */
	static @NotNull HistorySelection message(int id) {
		return new Message(id);
	}

	/**
	 * Selects the {@code amount} oldest messages, or every message when the history holds fewer.
	 */
	static @NotNull HistorySelection oldest(int amount) {
		return new Oldest(amount);
	}

	/**
	 * Selects every message the given player sent.
	 */
	static @NotNull HistorySelection of(@NotNull SocialismusPlayer player) {
		return new OfPlayer(player);
	}

	/**
	 * Selects every message sent by the player with the given username.
	 */
	static @NotNull HistorySelection byName(@NotNull String username) {
		return new ByName(username);
	}

	/**
	 * The message with the id {@code id}.
	 */
	record Message(int id) implements HistorySelection {
	}

	/**
	 * The {@code amount} oldest messages.
	 */
	record Oldest(int amount) implements HistorySelection {
	}

	/**
	 * Every message of {@code player}.
	 */
	record OfPlayer(@NotNull SocialismusPlayer player) implements HistorySelection {
	}

	/**
	 * Every message of the player named {@code username}.
	 */
	record ByName(@NotNull String username) implements HistorySelection {
	}
}
