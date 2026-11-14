package me.whereareiam.socialismus.module.mentionator.api.event;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.CancellableEvent;
import me.whereareiam.socialismus.event.base.SynchronousEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Fired on the server of a tagged player before they are notified, once their mode and the limits let the tag through.
 * Cancelling it, or emptying its notifications, keeps them silent; the tag stays highlighted in chat.
 */
@Getter
@ToString
public class PlayerTaggedEvent implements SynchronousEvent, CancellableEvent {
	/**
	 * The tagged player.
	 */
	private final @NotNull SocialismusPlayer player;

	/**
	 * The unique id of the player who tagged them.
	 */
	private final @NotNull UUID senderId;

	/**
	 * The name of the player who tagged them.
	 */
	private final @NotNull String senderName;

	/**
	 * The ids of the tags that tag them.
	 */
	private final @NotNull List<String> tags;

	/**
	 * The ids of the notifications they get, in order; changing the list changes what they get.
	 */
	private final @NotNull List<String> notifications;

	@Setter
	private boolean cancelled;

	public PlayerTaggedEvent(
			@NotNull SocialismusPlayer player,
			@NotNull UUID senderId,
			@NotNull String senderName,
			@NotNull List<String> tags,
			@NotNull List<String> notifications
	) {
		this.player = player;
		this.senderId = senderId;
		this.senderName = senderName;
		this.tags = tags;
		this.notifications = notifications;
	}
}
