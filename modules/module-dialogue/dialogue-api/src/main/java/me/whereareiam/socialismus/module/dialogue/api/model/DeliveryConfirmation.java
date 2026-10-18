package me.whereareiam.socialismus.module.dialogue.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;

import java.time.Instant;

/**
 * Tells the server a private message was sent on that another server of the network delivered it.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class DeliveryConfirmation {
	/**
	 * The identifier of the delivered message.
	 */
	private String id;

	/**
	 * The name of the player who wrote the message.
	 */
	private String senderName;

	/**
	 * The name of the player who received the message, as they spell themselves.
	 */
	private String recipientName;

	/**
	 * How far the message got.
	 */
	private DeliveryStatus status;

	/**
	 * The network identifier of the server the message was sent on.
	 */
	private String originServer;

	/**
	 * The network identifier of the server that delivered the message.
	 */
	private String deliveryServer;

	/**
	 * When the message was delivered.
	 */
	private Instant deliveryTime;
}
