package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import me.whereareiam.socialismus.integration.placeholderAPI.PlaceholderAPI;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

public class FormatterUtil {
    private final PlaceholderAPI placeholderAPI;

    @Inject
    public FormatterUtil(PlaceholderAPI placeholderAPI) {
        this.placeholderAPI = placeholderAPI;
    }

    public ComponentLike formatMessage(String message) {
        return formatMessage(null, message);
    }

    public ComponentLike formatMessage(Player player, String message) {
        final MiniMessage miniMessage = MiniMessage.miniMessage();

        message = cleanupMessage(message);
        message = hookIntegration(player, message);

        return miniMessage.deserialize(message);
    }

    private String cleanupMessage(String message) {
        // TODO
        return PlainTextComponentSerializer.plainText().deserialize(message).content();
    }

    private String hookIntegration(Player player, String message) {
        if (placeholderAPI.isEnabled()) {
            message = placeholderAPI.setPlaceholders(player, message);
        }

        return message;
    }
}
