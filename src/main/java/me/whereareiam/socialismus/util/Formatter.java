package me.whereareiam.socialismus.util;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Formatter {

    public ComponentLike formatMessage(String message) {
        final MiniMessage miniMessage = MiniMessage.miniMessage();

        message = cleanupMessage(message);
        message = hookIntegration(message);

        return miniMessage.deserialize(message);
    }

    private String cleanupMessage(String message) {
        // TODO
        return PlainTextComponentSerializer.plainText().deserialize(message).content();
    }

    private String hookIntegration(String message) {
        // TODO
        return message;
    }
}
