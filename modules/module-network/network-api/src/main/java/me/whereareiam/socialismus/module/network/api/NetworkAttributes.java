package me.whereareiam.socialismus.module.network.api;

import me.whereareiam.socialismus.model.attribute.AttributeKey;
import me.whereareiam.socialismus.module.network.api.model.Origin;

/**
 * The attributes SocialismusNetwork sets on what it relays, such as a chat message from another node.
 *
 * <pre>{@code
 * boolean fromAnotherNode = message.getAttribute(NetworkAttributes.ORIGIN).map(origin -> !origin.isLocal()).orElse(false);
 * }</pre>
 */
public final class NetworkAttributes {
	/**
	 * The node a relayed chat message was written on.
	 */
	public static final AttributeKey<Origin> ORIGIN = AttributeKey.create("socialismusnetwork", "origin", Origin.class);

	private NetworkAttributes() {
	}
}
