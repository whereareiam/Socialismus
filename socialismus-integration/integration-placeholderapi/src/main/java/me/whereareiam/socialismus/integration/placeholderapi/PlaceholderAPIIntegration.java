package me.whereareiam.socialismus.integration.placeholderapi;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.clip.placeholderapi.PlaceholderAPI;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.Serializers;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.MessageDecorator;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.PlaceholderIntegration;
import me.whereareiam.socialismus.integration.SerializerIntegration;
import me.whereareiam.socialismus.registry.Registry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Singleton
public class PlaceholderAPIIntegration implements SerializerIntegration, PlaceholderIntegration {
	@Inject
	public PlaceholderAPIIntegration(
			Registry<Integration> registry,
			Provider<SerializerEngine> serializerEngineProvider
	) {
		if (!isAvailable()) return;

		registry.register(this);
		registerDecorator(serializerEngineProvider.get());
	}

	@Override
	public String getName() {
		return "PlaceholderAPI";
	}

	@Override
	public boolean isAvailable() {
		try {
			Class.forName("me.clip.placeholderapi.PlaceholderAPI");
			return true;
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			return false;
		}
	}

	@Override
	public void registerDecorator(SerializerEngine engine) {
		Serializers.registerDecorator(engine, new PlaceholderAPIDecorator());
	}

	@Override
	public String resolve(UUID uniqueId, String text) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(uniqueId);
		return PlaceholderAPI.setPlaceholders(player, text);
	}

	/**
	 * Inner decorator controlled by this integration.
	 * Handles placeholder resolution in the serialization pipeline.
	 */
	private class PlaceholderAPIDecorator implements MessageDecorator {
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
			return PlaceholderAPIIntegration.this.isAvailable();
		}
	}
}
