package me.whereareiam.socialismus.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.listener.player.PlayerAsyncChatListener;
import me.whereareiam.socialismus.service.player.PlayerAsyncChatService;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class ListenerRegistrar {
    private final Injector injector;
    private final Plugin plugin;

    private final PlayerAsyncChatService playerAsyncChatService;

    @Inject
    public ListenerRegistrar(Injector injector, Plugin plugin, PlayerAsyncChatService playerAsyncChatService) {
        this.injector = injector;
        this.plugin = plugin;
        this.playerAsyncChatService = playerAsyncChatService;
    }

    public void registerListeners() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        boolean chatListenerRequired = playerAsyncChatService.isChatListenerRequired();

        if (chatListenerRequired) {
            PlayerAsyncChatListener playerAsyncChatListener = injector.getInstance(PlayerAsyncChatListener.class);
            pluginManager.registerEvents(playerAsyncChatListener, plugin);
        }
    }
}
