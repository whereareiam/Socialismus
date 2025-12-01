package me.whereareiam.socialismus.input.event.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.input.event.base.Event;
import me.whereareiam.socialismus.model.chat.Chat;

/**
 * Event that is triggered when a chat channel is being updated.
 * This event contains both the new and old states of the chat channel
 * and can be cancelled to prevent the updater from occurring.
 *
 * <p>Implements both {@link Event} and {@link CancellableEvent} interfaces to
 * provide event handling and cancellation functionality.</p>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class ChatUpdatedEvent implements Event, CancellableEvent {
	/**
	 * The new state of the chat channel after the updater.
	 */
	private final Chat chat;

	/**
	 * The previous state of the chat channel before the updater.
	 */
	private final Chat oldChat;

	/**
	 * The cancelled state of the event.
	 */
	private boolean cancelled;
}