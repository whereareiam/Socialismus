package me.whereareiam.socialismus.core.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.integration.IntegrationManager;
import me.whereareiam.socialismus.core.module.swapper.SwapperModule;
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
	}

	public void handleJoinEvent(Player player) {
		if (integrationManager.isIntegrationEnabled("ProtocolLib")) {
			final SwapperModule swapperModule = injector.getInstance(SwapperModule.class);
			swapperModule.suggestSwappers(player);
		} else {
			loggerUtil.warning("You can't use the Swapper suggestion without ProtocolLib!");
		}
	}

}

