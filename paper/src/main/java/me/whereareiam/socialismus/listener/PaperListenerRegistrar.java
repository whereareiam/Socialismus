package me.whereareiam.socialismus.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.listener.base.BaseListenerRegistrar;
import me.whereareiam.socialismus.service.ChatService;
import org.bukkit.plugin.Plugin;

@Singleton
public class PaperListenerRegistrar extends BaseListenerRegistrar {
    @Inject
    public PaperListenerRegistrar(Injector injector, Plugin plugin, ChatService chatService) {
        super(injector, plugin, chatService);
    }

    @Override
    protected PlayerChatListener createPlayerChatListener() {
        return injector.getInstance(AsyncChatListener.class);
    }
}
