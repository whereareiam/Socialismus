package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import me.whereareiam.socialismus.integration.placeholderAPI.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class FormatterUtil {
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(&[a-fk-or0-9])|(§[a-fk-or0-9])|(<#[0-9a-fA-F]{6}>)|(<(/)?[a-z]+>)");
    private static PlaceholderAPI placeholderAPI;

    @Inject
    public FormatterUtil(PlaceholderAPI placeholderAPI) {
        FormatterUtil.placeholderAPI = placeholderAPI;
    }

    public static String cleanupMessage(String message) {
        return COLOR_CODE_PATTERN.matcher(message).replaceAll("");
    }

    public static Component formatMessage(String message) {
        return formatMessage(null, message);
    }

    public static Component formatMessage(Player player, String message) {
        final MiniMessage miniMessage = MiniMessage.miniMessage();

        message = hookIntegration(player, message);

        return miniMessage.deserialize(message);
    }

    public static String hookIntegration(Player player, String message) {
        if (placeholderAPI.isEnabled()) {
            message = placeholderAPI.setPlaceholders(player, message);
        }

        // TODO Integration types

        return message;
    }
}
