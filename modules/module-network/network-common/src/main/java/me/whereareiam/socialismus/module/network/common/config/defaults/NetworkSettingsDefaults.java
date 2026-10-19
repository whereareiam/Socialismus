package me.whereareiam.socialismus.module.network.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.network.common.config.NetworkSettings;
import me.whereareiam.socialismus.type.PlatformType;

import java.time.Duration;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class NetworkSettingsDefaults implements DefaultsProvider<NetworkSettings> {
	@Override
	public NetworkSettings supply(NetworkSettings settings) {
		// Written once on the first start and kept from then on, so the node keeps its id across restarts.
		String role = PlatformType.isProxy() ? "proxy" : "server";
		settings.setNode(role + "-" + HexFormat.of().toHexDigits(ThreadLocalRandom.current().nextInt()));
		settings.setLease(Duration.ofSeconds(15));

		return settings;
	}
}
