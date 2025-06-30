package me.whereareiam.socialismus.adapter.config.template;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.Event;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.api.type.EventPriority;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.type.SerializationType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class SettingsTemplate implements DefaultConfig<Settings> {
	@Override
	public Settings getDefault() {
		Settings settings = new Settings();

		// Default values
		settings.setLevel(2);
		settings.setSerializer(SerializationType.MINIMESSAGE);

		Settings.Synchronization synchronization = new Settings.Synchronization();
		synchronization.setEnabled(false);
		synchronization.setIdentifier(UUID.randomUUID().toString());

		settings.setSynchronization(synchronization);

		Settings.Miscellaneous misc = new Settings.Miscellaneous();
		misc.setDisableJoinNotification(true);
		misc.setDisableQuitNotification(true);
		misc.setAllowLegacyParsing(false);
		misc.setAllowBrigadierCommands(false);
		misc.setVanillaSending(true);
		misc.setCommandsPerPage(7);

		settings.setMisc(misc);

		Settings.Updater updater = new Settings.Updater();
		updater.setCheckForUpdates(true);
		updater.setWarnAboutUpdates(true);
		updater.setWarnAboutDevBuilds(true);
		updater.setInterval(1);

		settings.setUpdater(updater);

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

		priorities.put("org.bukkit.event.player.PlayerLoginEvent", event);
		priorities.put("org.bukkit.event.player.PlayerJoinEvent", event);
		priorities.put("org.bukkit.event.player.PlayerQuitEvent", event);
		priorities.put("org.bukkit.event.player.PlayerChangedWorldEvent", event);
		priorities.put("org.bukkit.event.player.AsyncPlayerChatEvent", event);

		return priorities;
	}

	private Map<String, Event> getPrioritiesForPaper() {
		Map<String, Event> priorities = new HashMap<>();

		Event event = Event.builder().register(true).priority(EventPriority.LOWEST).build();

		priorities.put("org.bukkit.event.player.PlayerLoginEvent", event);
		priorities.put("org.bukkit.event.player.PlayerJoinEvent", event);
		priorities.put("org.bukkit.event.player.PlayerQuitEvent", event);
		priorities.put("org.bukkit.event.player.PlayerChangedWorldEvent", event);
		priorities.put("io.papermc.paper.event.player.AsyncChatEvent", event);

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
