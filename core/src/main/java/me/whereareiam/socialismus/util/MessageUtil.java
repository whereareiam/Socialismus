package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.platform.PlatformType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Singleton
public class MessageUtil {
    private final LoggerUtil loggerUtil;
    private final FormatterUtil formatterUtil;
    private BukkitAudiences bukkitAudiences;

    @Inject
    public MessageUtil(LoggerUtil loggerUtil, FormatterUtil formatterUtil, Plugin plugin) {
        this.loggerUtil = loggerUtil;
        this.formatterUtil = formatterUtil;

        if (PlatformType.getCurrentPlatform() == PlatformType.SPIGOT)
            bukkitAudiences = BukkitAudiences.create(plugin);

        loggerUtil.trace("Initializing class: " + this);
    }

    public void sendMessage(Player sender, String message) {
        Component messageComponent = formatterUtil.formatMessage(sender, message);
        sendMessage(sender, messageComponent);
    }

    public void sendMessage(Player sender, Component message) {
        Audience audience;
        if (PlatformType.getCurrentPlatform() == PlatformType.SPIGOT) {
            audience = bukkitAudiences.sender(sender);
        } else {
            audience = (Audience) sender;
        }
        audience.sendMessage(message);
    }

    public void close() {
        loggerUtil.debug("Closing BukkitAudiences");
        if (bukkitAudiences != null) {
            bukkitAudiences.close();
        }
    }
}
