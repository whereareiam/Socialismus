package me.whereareiam.socialismus.module.dialogue.api;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Sends private messages between two players, on this server or, when Dialogue syncs, on another one of the
 * network. Every message joins the conversation of its two participants, which {@link #reply} answers.
 */
public interface DialogueService {
	/**
	 * Sends a private message, which reaches the recipient in the first of their {@code received} formats they
	 * meet and is shown to the sender in their {@code sent} format. The sender is told instead when they write to
	 * themselves, when {@code messaging.requirements} do not let them write to the recipient, or when the recipient
	 * is not online.
	 *
	 * @param sender        the player who writes
	 * @param recipientName the name of the player to write to, in any casing
	 * @param content       what the sender writes
	 */
	void send(@NotNull SocialismusPlayer sender, @NotNull String recipientName, @NotNull String content);

	/**
	 * Sends a private message to the partner of the sender's most recent conversation, as {@link #send} does. The
	 * sender is told when that conversation is older than {@code messaging.reply.timeout} or there is none, along
	 * with their recent conversations, and when the partner went offline and {@code messaging.reply.toOffline} is
	 * off.
	 *
	 * @param sender  the player who replies
	 * @param content what the sender writes
	 */
	void reply(@NotNull SocialismusPlayer sender, @NotNull String content);
}
