package me.whereareiam.socialismus.integration.packetevents;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.type.IntegrationScope;

@Singleton
public class PacketEventsIntegration implements Integration {
	private boolean initialized;

	@Override
	public String getName() {
		return "PacketEvents";
	}

	@Override
	public boolean isAvailable() {
		try {
			Class.forName("com.github.retrooper.packetevents.PacketEventsAPI");

			return true;
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			return false;
		}
	}

	@Override
	public IntegrationScope scope() {
		return IntegrationScope.STARTUP_ONLY;
	}

	@Override
	public synchronized void initialize(Registry<Integration> registry) {
		if (initialized) return;
		if (!isAvailable()) return;
		registry.register(this);
		initialized = true;
	}
}
