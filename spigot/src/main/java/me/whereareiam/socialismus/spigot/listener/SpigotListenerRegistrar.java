package me.whereareiam.socialismus.spigot.listener;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.listener.ChatListener;
import me.whereareiam.socialismus.core.listener.base.BaseListenerRegistrar;
import me.whereareiam.socialismus.core.listener.state.ChatListenerState;
import me.whereareiam.socialismus.core.listener.state.JoinListenerState;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.spigot.listener.player.AsyncChatListener;
import org.bukkit.plugin.Plugin;

@Singleton
public class SpigotListenerRegistrar extends BaseListenerRegistrar {
	@Inject
	public SpigotListenerRegistrar(Injector injector, LoggerUtil loggerUtil, Plugin plugin,
								   ChatListenerState chatListenerState, JoinListenerState joinListenerState) {
		super(injector, loggerUtil, plugin, chatListenerState, joinListenerState);

		loggerUtil.trace("Initializing class: " + this);
	}

	@Override
	protected ChatListener createChatListener() {
		return injector.getInstance(AsyncChatListener.class);
	}
}
