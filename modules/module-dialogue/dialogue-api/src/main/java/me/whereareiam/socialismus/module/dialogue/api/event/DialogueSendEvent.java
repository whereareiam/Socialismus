package me.whereareiam.socialismus.module.dialogue.api.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.CancellableEvent;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.event.base.SynchronousEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired on the sender's server when a player sends a private message, after Dialogue's own checks and before the
 * message is delivered. Cancelling it stops the message; the listener tells the sender why, if it wants to.
 *
 * <p>This is how an add-on acts on private messages without Dialogue knowing what it checks, such as an add-on for a
 * punishment plugin that keeps muted players from sending them:
 * <pre>{@code
 * public final class MutedSenders implements EventListener {
 *     @SocialisticEvent
 *     public void onSend(DialogueSendEvent event) {
 *         if (!punishments.isMuted(event.getSender().getUniqueId())) return;
 *
 *         event.setCancelled(true);
 *         event.getSender().sendMessage(Component.text("You are muted."));
 *     }
 * }
 *
 * eventManager.register(new MutedSenders());
 * }</pre>
 */
@Getter
@Setter
@ToString
public class DialogueSendEvent implements Event, CancellableEvent, SynchronousEvent {
	/**
	 * The player who sends the message.
	 */
	private final @NotNull SocialismusPlayer sender;

	/**
	 * The name of the player the message is for, as they spell it when they are online here.
	 */
	private final @NotNull String recipientName;

	/**
	 * The recipient, or {@code null} when they are not on this server.
	 */
	private final @Nullable SocialismusPlayer recipient;

	/**
	 * What is sent; a listener may change it.
	 */
	private @NotNull String content;

	/**
	 * Whether the message is stopped.
	 */
	private boolean cancelled;

	public DialogueSendEvent(
			@NotNull SocialismusPlayer sender,
			@NotNull String recipientName,
			@Nullable SocialismusPlayer recipient,
			@NotNull String content
	) {
		this.sender = sender;
		this.recipientName = recipientName;
		this.recipient = recipient;
		this.content = content;
	}
}
