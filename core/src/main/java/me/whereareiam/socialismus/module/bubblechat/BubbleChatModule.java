package me.whereareiam.socialismus.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;

@Singleton
public class BubbleChatModule implements Module {
    private final SettingsConfig settingsConfig;
    private boolean moduleStatus;

    @Inject
    public BubbleChatModule(LoggerUtil loggerUtil, Plugin plugin, SettingsConfig settingsConfig, BubbleChatConfig bubbleChatConfig) {
        this.settingsConfig = settingsConfig;
        Path modulePath = plugin.getDataFolder().toPath().resolve("modules");

        loggerUtil.trace("Initializing class: " + this);

        bubbleChatConfig.reload(modulePath.resolve("bubblechat.yml"));
    }

    @Override
    public void initialize() {
        moduleStatus = true;
    }

    @Override
    public boolean isEnabled() {
        return moduleStatus == settingsConfig.modules.bubblechat;
    }

    @Override
    public void reload() {
    }
}
