package me.whereareiam.socialismus.platform.paper.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import me.whereareiam.socialismus.common.CommonListenerRegistrar;
import me.whereareiam.socialismus.platform.paper.listener.activity.PlayerWorldChangeListener;
import me.whereareiam.socialismus.platform.paper.listener.chat.PlayerChatListener;
import me.whereareiam.socialismus.platform.paper.listener.connection.PlayerJoinListener;
import me.whereareiam.socialismus.platform.paper.listener.connection.PlayerLoginListener;
import me.whereareiam.socialismus.platform.paper.listener.connection.PlayerQuitListener;
import me.whereareiam.socialismus.platform.paper.util.PaperUtil;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

@Singleton
public class PaperListenerRegistrar extends CommonListenerRegistrar {
    private final Injector injector;
    private final Plugin plugin;
    private final PluginManager pluginManager;

    @Inject
    public PaperListenerRegistrar(Injector injector, Provider<Settings> settingsProvider, Plugin plugin, PluginManager pluginManager) {
        super(settingsProvider, injector.getInstance(LoggingHelper.class));
        this.injector = injector;
        this.plugin = plugin;
        this.pluginManager = pluginManager;
    }

    @Override
    public void registerListeners() {
        registerListener(AsyncChatEvent.class, injector.getInstance(PlayerChatListener.class));
        registerListener(PlayerChangedWorldEvent.class, injector.getInstance(PlayerWorldChangeListener.class));
        registerListener(PlayerQuitEvent.class, injector.getInstance(PlayerQuitListener.class));
        registerListener(PlayerJoinEvent.class, injector.getInstance(PlayerJoinListener.class));
        registerListener(PlayerLoginEvent.class, injector.getInstance(PlayerLoginListener.class));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void registerListener(Class<T> eventClass, DynamicListener<T> listener) {
        if (settings.get().getListeners().getEvents().get(eventClass.getName()) == null
                || !settings.get().getListeners().getEvents().get(eventClass.getName()).isRegister()) return;
        loggingHelper.debug("Registering listener for event " + eventClass.getName());

        pluginManager.registerEvent(
                (Class<? extends Event>) eventClass,
                new Listener() {},
                PaperUtil.of(determinePriority(eventClass)),
                (l, e) -> listener.onEvent(eventClass.cast(e)),
                plugin
        );
    }
}
