package me.whereareiam.socialismus.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.feature.swapper.SwapperManager;
import org.bukkit.entity.Player;

@Singleton
public class JoinHandler {
    private final SettingsConfig settingsConfig;
    private final SwapperManager swapperManager;

    @Inject
    public JoinHandler(SettingsConfig settingsConfig, SwapperManager swapperManager) {
        this.settingsConfig = settingsConfig;
        this.swapperManager = swapperManager;
    }

    public void handleJoinEvent(Player player) {
        if (settingsConfig.features.swapper.suggest) {
            swapperManager.suggestSwappers(player);
        }
    }

}

