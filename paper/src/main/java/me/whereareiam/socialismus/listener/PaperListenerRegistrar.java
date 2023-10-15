package me.whereareiam.socialismus.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.listener.base.BaseListenerRegistrar;
import me.whereareiam.socialismus.listener.player.AsyncChatListener;
import me.whereareiam.socialismus.listener.state.ChatListenerState;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

@Singleton
public class PaperListenerRegistrar extends BaseListenerRegistrar {
    @Inject
    public PaperListenerRegistrar(Injector injector, LoggerUtil loggerUtil, Plugin plugin,
                                  ChatListenerState chatListenerState) {
        super(injector, loggerUtil, plugin, chatListenerState);
    }

    @Override
    protected PlayerChatListener createPlayerChatListener() {
        return injector.getInstance(AsyncChatListener.class);
    }
}
