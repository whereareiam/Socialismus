package me.whereareiam.socialismus.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.Event;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.type.EventPriority;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.Version;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class SettingsDefaults implements DefaultsProvider<Settings> {
	@Override
	public Settings supply(Settings settings) {
		settings.setLevel(2);

		Settings.Serialization serialization = new Settings.Serialization();
		serialization.setType("MINIMESSAGE");
		serialization.setEnableLegacyColors(false);
		settings.setSerialization(serialization);

		Settings.Vanilla vanilla = new Settings.Vanilla();
		vanilla.setJoinMessage(false);
		vanilla.setQuitMessage(false);
		vanilla.setSending(true);
		settings.setVanilla(vanilla);

		Settings.Listeners listeners = new Settings.Listeners();
		if (PlatformType.isProxy())
			listeners.setEvents(getPrioritiesForProxy());

		if (PlatformType.isAtLeast(PlatformType.PAPER))
			listeners.setEvents(getPrioritiesForPaper());
		else if (PlatformType.isAtLeast(PlatformType.BUKKIT))
			listeners.setEvents(getPrioritiesForBukkit());

		settings.setListeners(listeners);
		return settings;
	}

	private Map<String, Event> getPrioritiesForBukkit() {
		Map<String, Event> priorities = new HashMap<>();
		Event event = Event.builder().register(true).priority(EventPriority.LOWEST).build();

		priorities.put("org.bukkit.event.player.PlayerJoinEvent", event);
		priorities.put("org.bukkit.event.player.PlayerQuitEvent", event);
		priorities.put("org.bukkit.event.player.PlayerChangedWorldEvent", event);
		priorities.put("org.bukkit.event.player.AsyncPlayerChatEvent", event);
		return priorities;
	}

	private Map<String, Event> getPrioritiesForPaper() {
		Map<String, Event> priorities = new HashMap<>();
		Event event = Event.builder().register(true).priority(EventPriority.LOWEST).build();

		priorities.put("org.bukkit.event.player.PlayerJoinEvent", event);
		priorities.put("org.bukkit.event.player.PlayerQuitEvent", event);
		priorities.put("org.bukkit.event.player.PlayerChangedWorldEvent", event);
		priorities.put("io.papermc.paper.event.player.AsyncChatEvent", event);

		if (Constants.SERVER_VERSION.isAtLeast(Version.V_1_21_6))
			priorities.put("io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent", event);

		return priorities;
	}

	private Map<String, Event> getPrioritiesForProxy() {
		Map<String, Event> priorities = new HashMap<>();
		Event event = Event.builder().register(true).priority(EventPriority.LOWEST).build();

		priorities.put("com.velocitypowered.api.event.player.ServerPostConnectEvent", event);
		priorities.put("com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent", event);
		priorities.put("com.velocitypowered.api.event.player.PlayerChatEvent", event);
		priorities.put("com.velocitypowered.api.event.connection.DisconnectEvent", event);
		return priorities;
	}
}
