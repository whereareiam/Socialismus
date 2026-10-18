package me.whereareiam.socialismus.module.dialogue.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;

import java.time.Instant;

/**
 * A private message from one player to another, as it is delivered, sent to the other servers of the network and
 * kept in its conversation.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Message {
	/**
	 * Unique identifier of the message, which a delivery confirmation names.
	 */
	private String id;

	/**
	 * The player who wrote the message.
	 */
	private SocialismusPlayer sender;

	/**
	 * The name of the player the message is for.
	 */
	private String recipientName;

	/**
	 * What the sender wrote.
	 */
	private String content;

	/**
	 * When the message was sent.
	 */
	private Instant timestamp;

	/**
	 * How far the message got.
	 */
	private DeliveryStatus deliveryStatus;

	/**
	 * The network identifier of the server the message was sent on.
	 */
	private String origin;

	/**
	 * The identifier of the conversation the message belongs to.
	 */
	private String conversationId;

	/**
	 * Whether the sender reaches recipients who turned private messages off, as {@code control.toggle.bypass}
	 * decided on the sender's server, where the sender's requirements can be checked.
	 */
	private boolean bypassesToggle;

	/**
	 * Whether the sender cannot be ignored, as {@code control.ignore.exempt} decided on the sender's server.
	 */
	private boolean exemptFromIgnore;
}
