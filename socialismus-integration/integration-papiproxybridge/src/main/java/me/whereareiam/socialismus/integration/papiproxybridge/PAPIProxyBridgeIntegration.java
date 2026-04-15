package me.whereareiam.socialismus.integration.papiproxybridge;

import com.google.inject.Singleton;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.Serializers;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.MessageDecorator;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.PlaceholderIntegration;
import me.whereareiam.socialismus.integration.SerializerIntegration;
import me.whereareiam.socialismus.registry.base.Registry;
import net.william278.papiproxybridge.api.PlaceholderAPI;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
public class PAPIProxyBridgeIntegration implements SerializerIntegration, PlaceholderIntegration {
	private PlaceholderAPI placeholderAPI;
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
		if (placeholderAPI == null) {
			placeholderAPI = PlaceholderAPI.createInstance();
		}

		registry.register(this);
		initialized = true;
	}

	@Override
	public void registerDecorator(SerializerEngine engine) {
		Serializers.registerDecorator(engine, new PAPIProxyBridgeDecorator());
	}

	@Override
	public String resolve(UUID uniqueId, String text) {
		return placeholderAPI.formatPlaceholders(text, uniqueId).getNow(text);
	}

	/**
	 * Inner decorator controlled by this integration.
	 * Handles placeholder resolution in the serialization pipeline.
	 */
	private class PAPIProxyBridgeDecorator implements MessageDecorator {
		@Override
		@NotNull
		public SerializerContent decorate(@NotNull SerializerContent content) {
			Actor receiver = content.getReceiver();
			if (receiver == null) return content;

			String processed = resolve(receiver.getUniqueId(), content.getMessage());
			content.setMessage(processed);

			return content;
		}

		@Override
		public boolean isAvailable() {
			return PAPIProxyBridgeIntegration.this.isAvailable();
		}
	}
}
