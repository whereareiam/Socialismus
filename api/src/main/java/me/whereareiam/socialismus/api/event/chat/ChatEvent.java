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
}