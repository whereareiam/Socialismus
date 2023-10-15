package me.whereareiam.socialismus.listener.base;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.listener.ListenerRegistrar;
import me.whereareiam.socialismus.listener.PlayerChatListener;
import me.whereareiam.socialismus.service.ChatService;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public abstract class BaseListenerRegistrar implements ListenerRegistrar {
    protected final Injector injector;
    protected final LoggerUtil loggerUtil;
    protected final Plugin plugin;

    protected final ChatService chatService;

    @Inject
    public BaseListenerRegistrar(Injector injector, LoggerUtil loggerUtil, Plugin plugin,
                                 ChatService chatService) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.plugin = plugin;

        this.chatService = chatService;

        loggerUtil.trace("Initializing class: " + this);
    }

    @Override
    public void registerListeners() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        boolean chatListenerRequired = chatService.isChatListenerRequired();

        if (chatListenerRequired) {
            loggerUtil.debug("Registering chat listener");
            PlayerChatListener playerChatListener = createPlayerChatListener();
            pluginManager.registerEvents(playerChatListener, plugin);
        }
    }

    protected abstract PlayerChatListener createPlayerChatListener();
}