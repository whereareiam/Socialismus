package me.whereareiam.socialismus.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.feature.swapper.SwapperManager;
import org.bukkit.entity.Player;

@Singleton
public class JoinHandler {
    private final Injector injector;
    private final SettingsConfig settingsConfig;

    @Inject
    public JoinHandler(Injector injector, SettingsConfig settingsConfig) {
        this.injector = injector;
        this.settingsConfig = settingsConfig;
    }

    public void handleJoinEvent(Player player) {
        if (settingsConfig.features.swapper.suggest) {
            final SwapperManager swapperManager = injector.getInstance(SwapperManager.class);
            swapperManager.suggestSwappers(player);
        }
    }

}

