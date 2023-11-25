package me.whereareiam.socialismus.listener.base;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.listener.ChatListener;
import me.whereareiam.socialismus.listener.JoinListener;
import me.whereareiam.socialismus.listener.ListenerRegistrar;
import me.whereareiam.socialismus.listener.player.PlayerJoinListener;
import me.whereareiam.socialismus.listener.state.ChatListenerState;
import me.whereareiam.socialismus.listener.state.JoinListenerState;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public abstract class BaseListenerRegistrar implements ListenerRegistrar {
    protected final Injector injector;
    protected final LoggerUtil loggerUtil;
    protected final Plugin plugin;

    protected final ChatListenerState chatListenerState;
    protected final JoinListenerState joinListenerState;

    @Inject
    public BaseListenerRegistrar(Injector injector, LoggerUtil loggerUtil, Plugin plugin,
                                 ChatListenerState chatListenerState, JoinListenerState joinListenerState) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.plugin = plugin;

        this.chatListenerState = chatListenerState;
        this.joinListenerState = joinListenerState;

        loggerUtil.trace("Initializing class: " + this);
    }

    @Override
    public void registerListeners() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        boolean chatListenerRequired = ChatListenerState.isRequired();
        boolean joinListenerRequired = JoinListenerState.isRequired();

        if (chatListenerRequired) {
            loggerUtil.debug("Registering chat listener");
            ChatListener chatListener = createChatListener();
            pluginManager.registerEvents(chatListener, plugin);
        }

        if (joinListenerRequired) {
            loggerUtil.debug("Registering join listener");
            JoinListener joinListener = createJoinListener();
            pluginManager.registerEvents(joinListener, plugin);
        }
    }

    protected abstract ChatListener createChatListener();

    private JoinListener createJoinListener() {
        return injector.getInstance(PlayerJoinListener.class);
    }
}