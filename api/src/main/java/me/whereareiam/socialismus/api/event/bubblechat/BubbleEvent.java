package me.whereareiam.socialismus.api.event.bubblechat;

import me.whereareiam.socialismus.api.model.BubbleMessage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @since 1.2.0
 */
public abstract class BubbleEvent extends Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	/**
	 * BubbleMessage object that is being sent to the bubble chat.
	 */
	protected BubbleMessage bubbleMessage;

	/**
	 * Creates a new bubble event.
	 *
	 * @param bubbleMessage The message that is being sent to the bubble chat.
	 * @since 1.2.0
	 */
	public BubbleEvent(BubbleMessage bubbleMessage) {
		super(true);

		this.bubbleMessage = bubbleMessage;
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
	 * Gets the message that is being sent to the bubble chat.
	 * <p>
	 * BubbleMessage#getDisplayTime() allows you to get the display time.
	 * <p>
	 * BubbleMessage#getContent() allows you to get the message.
	 * <p>
	 * BubbleMessage#getSender() allows you to get the sender.
	 * <p>
	 * BubbleMessage#getRecipients() allows you to get the recipients.
	 *
	 * @return The message that is being sent to the bubble chat.
	 * @since 1.2.0
	 */
	public BubbleMessage getBubbleMessage() {
		return bubbleMessage;
	}

	/**
	 * Sets the message that is being sent to the bubble chat.
	 * <p>
	 * BubbleMessage#setDisplayTime(int) allows you to change the display time.
	 * <p>
	 * BubbleMessage#setContent(Component) allows you to change the message.
	 * <p>
	 * BubbleMessage#setSender(Player) allows you to change the sender.
	 * <p>
	 * BubbleMessage#setRecipients(Collection) allows you to change the recipients.
	 *
	 * @param bubbleMessage The message that is being sent to the bubble chat.
	 * @since 1.2.0
	 */
	public void setBubbleMessage(BubbleMessage bubbleMessage) {
		this.bubbleMessage = bubbleMessage;
	}
}
