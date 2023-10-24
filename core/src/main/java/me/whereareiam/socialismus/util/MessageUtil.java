package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.platform.PlatformMessageSender;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Singleton
public class MessageUtil {
    private final LoggerUtil loggerUtil;
    private final FormatterUtil formatterUtil;
    private final PlatformMessageSender platformMessageSender;

    @Inject
    public MessageUtil(LoggerUtil loggerUtil, FormatterUtil formatterUtil, PlatformMessageSender platformMessageSender, PlatformMessageSender platformMessageSender1) {
        this.loggerUtil = loggerUtil;
        this.formatterUtil = formatterUtil;
        this.platformMessageSender = platformMessageSender1;
    }

    public void sendMessage(Player sender, String message) {
        sendMessage(sender, formatterUtil.formatMessage(sender, message));
    }

    public void sendMessage(Player sender, Component message) {
        platformMessageSender.sendMessage(sender, message);
    }
}
