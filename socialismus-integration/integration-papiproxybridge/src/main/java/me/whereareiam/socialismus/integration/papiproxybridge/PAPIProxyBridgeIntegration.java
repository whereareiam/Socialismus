package me.whereareiam.socialismus.integration.papiproxybridge;

import com.google.inject.Singleton;
import me.whereareiam.keystone.Serializers;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.placeholder.PlaceholderIntegration;
import me.whereareiam.socialismus.integration.placeholder.PlaceholderMessageDecorator;
import me.whereareiam.socialismus.type.PlaceholderResolutionMode;
import me.whereareiam.socialismus.integration.SerializerIntegration;
import me.whereareiam.socialismus.registry.base.Registry;
import net.william278.papiproxybridge.api.PlaceholderAPI;

import java.util.concurrent.CompletableFuture;
import java.util.UUID;

@Singleton
public class PAPIProxyBridgeIntegration implements PlaceholderIntegration, SerializerIntegration {
	private PlaceholderResolver resolver;
	private boolean initialized;

	@Override
	public String getName() {
		return "PAPIProxyBridge";
	}

	@Override
	public boolean isAvailable() {
		try {
			Class.forName("net.william278.papiproxybridge.api.PlaceholderAPI");
			return true;
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			return false;
		}
	}

	@Override
	public synchronized void initialize(Registry<Integration> registry) {
		if (initialized || !isAvailable()) return;
		PlaceholderAPI placeholderAPI = PlaceholderAPI.createInstance();
		resolver = placeholderAPI::formatPlaceholders;
		registry.register(this);
		initialized = true;
	}

	@Override
	public String resolve(UUID uniqueId, String text) {
		if (resolver == null) return text;
		return resolver.resolve(text, uniqueId).getNow(text);
	}

	@Override
	public void registerDecorator(SerializerEngine engine) {
		Serializers.registerDecorator(engine, new PlaceholderMessageDecorator(this));
	}

	@Override
	public PlaceholderResolutionMode resolutionMode() {
		return PlaceholderResolutionMode.CHAINED;
	}

	@FunctionalInterface
	private interface PlaceholderResolver {
		CompletableFuture<String> resolve(String text, UUID uniqueId);
	}
}
