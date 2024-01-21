package me.whereareiam.socialismus.api.event.chat;

import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.2.0
 */
public abstract class ChatEvent extends Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	/**
	 * ChatMessage object that is being sent to the chat.
	 */
	protected ChatMessage chatMessage;

	/**
	 * Creates a new chat event.
	 *
	 * @param chatMessage The message that is being sent to the chat.
	 * @since 1.2.0
	 */
	public ChatEvent(ChatMessage chatMessage) {
		super(true);

		this.chatMessage = chatMessage;
	}

	/**
	 * @return The handler list.
	 */
	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	/**
	 * @return The handler list.
	 */
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	/**
	 * Gets the message that is being sent to the chat.
	 * <p>
	 * ChatMessage#getContent() allows you to get the message.
	 * <p>
	 * ChatMessage#getSender() allows you to get the sender.
	 * <p>
	 * ChatMessage#getRecipients() allows you to get the recipients.
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
	 * <p>
	 * ChatMessage#setRecipients(Players) allows you to change the recipients.
	 *
	 * @param chatMessage The message that is being sent to the chat.
	 * @since 1.2.0
	 */
	public void setChatMessage(ChatMessage chatMessage) {
		this.chatMessage = chatMessage;
	}
}