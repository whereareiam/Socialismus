package me.whereareiam.socialismus.api.event.chat;

import me.whereareiam.socialismus.api.model.ChatMessage;
import org.bukkit.event.Cancellable;

/**
 * This event is called before a message is sent to the chat.
 * <p>
 * Allows to modify the ChatMessage object before it is sent to the chat, by
 * changing the message or the recipients.
 * <p>
 * Using this event we can add or remove players for requirement validation.
 *
 * @since 1.0.0
 */
public class BeforeChatSendMessageEvent extends ChatEvent implements Cancellable {
	private boolean cancelled;

	/**
	 * Constructor for the event.
	 *
	 * @param chatMessage The ChatMessage object that is about to be sent to the chat.
	 * @since 1.0.0
	 */
	public BeforeChatSendMessageEvent(ChatMessage chatMessage) {
		super(chatMessage);
	}

	/**
	 * Check if the event is cancelled.
	 *
	 * @return true if the event is cancelled, false otherwise.
	 * @since 1.2.0
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Set the event to cancelled or not.
	 * <p>
	 * If the event is cancelled, the message will not be sent to the chat
	 * and other modules won't do any further processing on the message.
	 *
	 * @param cancelled true if the event should be cancelled, false otherwise.
	 * @since 1.2.0
	 */
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
