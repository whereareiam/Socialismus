package me.whereareiam.socialismus.module.link.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * How the proxy and its servers connect, {@code settings} in SocialismusLink's directory. The proxy listens on
 * {@link #listen}, and every server connects to it at {@link #proxy}; all of them need the same {@link #secret}.
 * Changes take effect on the next start.
 */
@Getter
@Setter
@ToString
public class LinkSettings {
	/**
	 * What servers must know to join the link. The proxy's first start writes a random one; copy it to every server.
	 */
	@ToString.Exclude
	private String secret;

	/**
	 * On the proxy: where it listens for its servers.
	 */
	private Address listen;

	/**
	 * On a server: where the proxy listens.
	 */
	private Address proxy;

	/**
	 * A host and a port.
	 */
	@Getter
	@Setter
	@ToString
	public static class Address {
		private String host;
		private int port;
	}
}
