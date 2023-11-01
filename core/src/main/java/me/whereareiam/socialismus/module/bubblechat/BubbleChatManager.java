package me.whereareiam.socialismus.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;

@Singleton
public class BubbleChatManager implements Module {

    @Inject
    public BubbleChatManager(LoggerUtil loggerUtil, Plugin plugin, BubbleChatConfig bubbleChatConfig) {
        Path modulePath = plugin.getDataFolder().toPath().resolve("modules");

        loggerUtil.trace("Initializing class: " + this);

        bubbleChatConfig.reload(modulePath.resolve("bubblechat.yml"));
    }

    @Override
    public boolean requiresChatListener() {
        return true;
    }

    @Override
    public boolean requiresJoinListener() {
        return false;
    }
}
