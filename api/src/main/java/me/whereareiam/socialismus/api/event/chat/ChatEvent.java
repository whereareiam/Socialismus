package me.whereareiam.socialismus.api.event.chat;

import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @since 1.2.0
 */
public abstract class ChatEvent extends Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();
	protected ChatMessage chatMessage;
	protected Collection<? extends Player> recipients;

	/**
	 * Creates a new chat event.
	 *
	 * @param chatMessage The message that is being sent to the chat.
	 * @param recipients  The recipients of the message.
	 * @since 1.2.0
	 */
	public ChatEvent(ChatMessage chatMessage, Collection<? extends Player> recipients) {
		super(true);

		this.chatMessage = chatMessage;
		this.recipients = recipients;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	/**
	 * Gets the message that is being sent to the chat.
	 *
	 * @return The message that is being sent to the chat.
	 * @since 1.2.0
	 */
	public ChatMessage getChatMessage() {
		return chatMessage;
	}

	/**
	 * Sets the message that is being sent to the chat.
	 * <p>
	 * ChatMessage#setContent(Component) allows you to change the message.
	 * <p>
	 * ChatMessage#setSender(Player) allows you to change the sender.
	 *
	 * @param chatMessage The message that is being sent to the chat.
	 * @since 1.2.0
	 */
	public void setChatMessage(ChatMessage chatMessage) {
		this.chatMessage = chatMessage;
	}

	/**
	 * Gets the recipients of the message.
	 *
	 * @return The recipients of the message.
	 * @since 1.2.0
	 */
	public Collection<? extends Player> getRecipients() {
		return recipients;
	}

	/**
	 * Sets the recipients of the message.
	 * <p>
	 * By adding new players as recipients, the radius and other requirements will be ignored
	 * and the message will be directly sent to the player.
	 *
	 * @param recipients The recipients of the message.
	 * @since 1.2.0
	 */
	public void setRecipients(Collection<? extends Player> recipients) {
		this.recipients = recipients;
	}

	/**
	 * Adds a recipient to the message.
	 *
	 * @param player The recipient to add.
	 * @since 1.2.0
	 */
	public void removeRecipient(Player player) {
		recipients.remove(player);
	}
}