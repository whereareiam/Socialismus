package me.whereareiam.socialismus.module.dialogue.api.type;

/**
 * How far a private message got.
 */
public enum DeliveryStatus {
	/**
	 * Sent, but not delivered yet.
	 */
	PENDING_DELIVERY,

	/**
	 * Delivered to a player on the server it was sent on.
	 */
	DELIVERED_LOCALLY,

	/**
	 * Delivered by another server of the network.
	 */
	DELIVERED_REMOTELY,

	/**
	 * The recipient is not online anywhere Dialogue can reach.
	 */
	RECIPIENT_OFFLINE,

	/**
	 * The recipient has turned private messages off, and the sender does not bypass that.
	 */
	MESSAGES_OFF,

	/**
	 * The recipient ignores the sender.
	 */
	IGNORED
}
