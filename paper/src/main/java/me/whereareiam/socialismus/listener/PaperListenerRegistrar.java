package me.whereareiam.socialismus.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.listener.base.BaseListenerRegistrar;
import me.whereareiam.socialismus.service.ChatService;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

@Singleton
public class PaperListenerRegistrar extends BaseListenerRegistrar {
    @Inject
    public PaperListenerRegistrar(Injector injector, LoggerUtil loggerUtil, Plugin plugin, ChatService chatService) {
        super(injector, loggerUtil, plugin, chatService);
    }

    @Override
    protected PlayerChatListener createPlayerChatListener() {
        return injector.getInstance(AsyncChatListener.class);
    }
}
