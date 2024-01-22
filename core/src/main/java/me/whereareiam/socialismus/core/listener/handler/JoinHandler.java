package me.whereareiam.socialismus.core.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.integration.IntegrationManager;
import me.whereareiam.socialismus.core.module.swapper.SwapperService;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import org.bukkit.entity.Player;

@Singleton
public class JoinHandler {
	private final Injector injector;
	private final LoggerUtil loggerUtil;
	private final IntegrationManager integrationManager;

	@Inject
	public JoinHandler(Injector injector, LoggerUtil loggerUtil, IntegrationManager integrationManager) {
		this.injector = injector;
		this.loggerUtil = loggerUtil;
		this.integrationManager = integrationManager;

		loggerUtil.trace("Initializing class: " + this);
	}

	public void handleJoinEvent(Player player) {
		injector.getInstance(SwapperService.class).suggestSwappers(player);
	}

}

