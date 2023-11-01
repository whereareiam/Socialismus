package me.whereareiam.socialismus.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.integration.IntegrationManager;
import me.whereareiam.socialismus.module.swapper.SwapperManager;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.entity.Player;

@Singleton
public class JoinHandler {
    private final Injector injector;
    private final LoggerUtil loggerUtil;
    private final IntegrationManager integrationManager;
    private final SettingsConfig settingsConfig;

    @Inject
    public JoinHandler(Injector injector, LoggerUtil loggerUtil, IntegrationManager integrationManager,
                       SettingsConfig settingsConfig) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.integrationManager = integrationManager;
        this.settingsConfig = settingsConfig;
    }

    public void handleJoinEvent(Player player) {
        if (integrationManager.isIntegrationEnabled("ProtocolLib")) {
            final SwapperManager swapperManager = injector.getInstance(SwapperManager.class);
            if (settingsConfig.modules.swapper.suggest)
                swapperManager.suggestSwappers(player);
        } else {
            loggerUtil.warning("You can't use the Swapper suggestion without ProtocolLib!");
        }
    }

}

