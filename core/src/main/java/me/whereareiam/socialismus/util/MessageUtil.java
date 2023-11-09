package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.platform.PlatformMessageSender;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Singleton
public class MessageUtil {
    private static LoggerUtil loggerUtil;
    private static FormatterUtil formatterUtil;
    private static PlatformMessageSender platformMessageSender;

    @Inject
    public MessageUtil(LoggerUtil loggerUtil, FormatterUtil formatterUtil, PlatformMessageSender platformMessageSender) {
        MessageUtil.loggerUtil = loggerUtil;
        MessageUtil.formatterUtil = formatterUtil;
        MessageUtil.platformMessageSender = platformMessageSender;

        loggerUtil.trace("Initializing class: " + this);
    }

    public static void sendMessage(Player sender, String message) {
        sendMessage(sender, formatterUtil.formatMessage(sender, message));
    }

    public static void sendMessage(Player sender, Component message) {
        loggerUtil.debug("Sending message to " + sender.getName());
        loggerUtil.trace(message.toString());
        platformMessageSender.sendMessage(sender, message);
    }
}
