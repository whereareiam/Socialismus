package me.whereareiam.socialismus.platform.velocity.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import me.whereareiam.socialismus.common.CommonListenerRegistrar;
import me.whereareiam.socialismus.listener.DynamicListener;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.platform.velocity.VelocitySocialismus;
import me.whereareiam.socialismus.platform.velocity.listener.activity.ServerChangeListener;
import me.whereareiam.socialismus.platform.velocity.listener.chat.PlayerChatListener;
import me.whereareiam.socialismus.platform.velocity.listener.connection.PlayerJoinListener;
import me.whereareiam.socialismus.platform.velocity.listener.connection.PlayerQuitListener;
import me.whereareiam.socialismus.platform.velocity.util.VelocityUtil;

@Singleton
public class VelocityListenerRegistrar extends CommonListenerRegistrar {
	private final Injector injector;
	private final VelocitySocialismus plugin;
	private final EventManager eventManager;

	@Inject
	public VelocityListenerRegistrar(
			Injector injector,
			Provider<Settings> settingsProvider,
			VelocitySocialismus plugin,
			EventManager eventManager
	) {
		super(settingsProvider);
		this.injector = injector;
		this.plugin = plugin;
		this.eventManager = eventManager;
	}

	@Override
	@SuppressWarnings("UnstableApiUsage")
	public void registerListeners() {
		registerListener(PlayerChatEvent.class, injector.getInstance(PlayerChatListener.class));
		registerListener(ServerPostConnectEvent.class, injector.getInstance(ServerChangeListener.class));
		registerListener(DisconnectEvent.class, injector.getInstance(PlayerQuitListener.class));
		registerListener(PlayerChooseInitialServerEvent.class, injector.getInstance(PlayerJoinListener.class));
	}

	@Override
	public <T> void registerListener(Class<T> eventClass, DynamicListener<T> listener) {
		if (settings.get().getListeners().getEvents().get(eventClass.getName()) != null
				&& !settings.get().getListeners().getEvents().get(eventClass.getName()).isRegister()) return;
		Logger.debug("Registering listener for event " + eventClass.getName());

		eventManager.register(plugin, eventClass, VelocityUtil.of(determinePriority(eventClass)), listener::onEvent);
	}
}
