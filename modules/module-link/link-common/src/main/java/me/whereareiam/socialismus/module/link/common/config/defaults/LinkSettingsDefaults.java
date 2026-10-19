package me.whereareiam.socialismus.module.link.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.link.common.config.LinkSettings;

import java.security.SecureRandom;
import java.util.HexFormat;

@Singleton
public class LinkSettingsDefaults implements DefaultsProvider<LinkSettings> {
	private static final SecureRandom RANDOM = new SecureRandom();

	@Override
	public LinkSettings supply(LinkSettings settings) {
		// Written once on the first start and kept from then on.
		byte[] secret = new byte[24];
		RANDOM.nextBytes(secret);
		settings.setSecret(HexFormat.of().formatHex(secret));
		settings.setListen(address("0.0.0.0", 25580));
		settings.setProxy(address("127.0.0.1", 25580));

		return settings;
	}

	private static LinkSettings.Address address(String host, int port) {
		LinkSettings.Address address = new LinkSettings.Address();
		address.setHost(host);
		address.setPort(port);

		return address;
	}
}
