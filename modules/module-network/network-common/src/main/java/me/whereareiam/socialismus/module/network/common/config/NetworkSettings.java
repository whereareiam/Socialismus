package me.whereareiam.socialismus.module.network.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;

/**
 * How this node takes part in the network, {@code settings} in SocialismusNetwork's directory. Changes take effect
 * on the next start.
 */
@Getter
@Setter
@ToString
public class NetworkSettings {
	/**
	 * This node's id on the network, unique among its nodes. The first start writes a random one; give it a name of
	 * your own, such as {@code lobby-1}, to recognise it in the logs.
	 */
	private String node;

	/**
	 * How long a node's announcement and its leases live without being renewed: the time after which the others take
	 * over from a node that crashed. A node renews them three times as often.
	 */
	private Duration lease;

	/**
	 * What chat does on the network.
	 */
	private Chat chat;

	/**
	 * What chat does on the network.
	 */
	@Getter
	@Setter
	@ToString
	public static class Chat {
		/**
		 * Whether chat messages go to and come from the other nodes. A chat with a radius always stays on its node.
		 */
		private boolean enabled;

		/**
		 * Who handles the chat of the players on this server: the server itself, or the proxy in front of it, as when
		 * the proxy holds the chats and this server only takes the original messages back. Ignored on a proxy.
		 */
		private ChatOwner owner;

		/**
		 * Whether a message from another node keeps the format it was given there, instead of being formatted here.
		 */
		private boolean preserveFormat;

		/**
		 * Whether clearing the chat history clears it on the other nodes too.
		 */
		private boolean clearHistory;
	}

	/**
	 * Who handles the chat of a server's players.
	 */
	public enum ChatOwner {
		/** The server itself. */
		SERVER,
		/** The proxy in front of it. */
		PROXY
	}
}
