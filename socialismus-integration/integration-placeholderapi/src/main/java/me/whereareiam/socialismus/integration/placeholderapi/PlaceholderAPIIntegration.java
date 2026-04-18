package me.whereareiam.socialismus.integration.placeholderapi;

import com.google.inject.Singleton;
import me.clip.placeholderapi.PlaceholderAPI;
import me.whereareiam.keystone.Serializers;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.placeholder.PlaceholderIntegration;
import me.whereareiam.socialismus.integration.placeholder.PlaceholderMessageDecorator;
import me.whereareiam.socialismus.type.PlaceholderResolutionMode;
import me.whereareiam.socialismus.integration.SerializerIntegration;
import me.whereareiam.socialismus.registry.base.Registry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Singleton
public class PlaceholderAPIIntegration implements PlaceholderIntegration, SerializerIntegration {
	private boolean initialized;

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
	public synchronized void initialize(Registry<Integration> registry) {
		if (initialized) return;
		if (!isAvailable()) return;
		registry.register(this);
		initialized = true;
	}

	@Override
	public String resolve(UUID uniqueId, String text) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(uniqueId);
		return PlaceholderAPI.setPlaceholders(player, text);
	}

	@Override
	public void registerDecorator(SerializerEngine engine) {
		Serializers.registerDecorator(engine, new PlaceholderMessageDecorator(this));
	}

	@Override
	public PlaceholderResolutionMode resolutionMode() {
		return PlaceholderResolutionMode.CHAINED;
	}
}
