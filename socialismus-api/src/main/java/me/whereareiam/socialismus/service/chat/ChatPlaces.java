package me.whereareiam.socialismus.service.chat;

import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.style.TextStyle;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The bound places of one chat, found through {@link ChatConditions#of}: who may write in it, whom it reaches, which
 * of its formats a sender gets, and the style they write with.
 */
public interface ChatPlaces {
	/**
	 * Checks whether the sender of a message may write in the chat: its {@code requires}.
	 *
	 * @param message the message, with its sender
	 * @return the verdict; a failed one names the condition and its reason
	 */
	@NotNull Verdict writes(@NotNull ChatMessage message);

	/**
	 * Checks whether a message of the chat reaches a player: its {@code reaches}.
	 *
	 * @param recipient the player
	 * @param message   the message, with its sender
	 * @return the verdict; a failed one names the condition and its reason, which the sender is told when the
	 * message reaches nobody
	 */
	@NotNull Verdict reaches(@NotNull SocialismusPlayer recipient, @NotNull ChatMessage message);

	/**
	 * Chooses the format of a message: the last of the chat's formats whose {@code requires} the sender meets.
	 *
	 * @param message the message, with its sender
	 * @return the format, empty when the sender meets none
	 */
	@NotNull Optional<ChatFormat> format(@NotNull ChatMessage message);

	/**
	 * Chooses the style a sender writes with: the last of the chat's {@code parameters.styles} whose conditions they
	 * meet.
	 *
	 * @param sender the sender
	 * @return the style, {@link TextStyle#PLAIN} when they meet none or the chat accepts none
	 */
	@NotNull TextStyle style(@NotNull SocialismusPlayer sender);
}
