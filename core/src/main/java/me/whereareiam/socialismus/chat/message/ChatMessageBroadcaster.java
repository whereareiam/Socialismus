package me.whereareiam.socialismus.chat.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.chat.model.Chat;
import me.whereareiam.socialismus.util.DistanceCalculatorUtil;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

import java.util.List;

@Singleton
public class ChatMessageBroadcaster {
    private final LoggerUtil loggerUtil;
    private final FormatterUtil formatterUtil;

    @Inject
    public ChatMessageBroadcaster(LoggerUtil loggerUtil, FormatterUtil formatterUtil) {
        this.loggerUtil = loggerUtil;
        this.formatterUtil = formatterUtil;
    }

    public void broadcastMessage(ChatMessage chatMessage, Player recipient) {
        if (shouldSendMessage(chatMessage.getSender(), recipient, chatMessage.getChat().radius)) {
            Audience recipientAudience = (Audience) recipient;
            Component finalMessage = createFinalMessage(chatMessage);
            recipientAudience.sendMessage(finalMessage);

            loggerUtil.trace("Sent message to player: " + recipient.getName());
        }
    }

    private boolean shouldSendMessage(Player sender, Player recipient, int radius) {
        return sender.equals(recipient)
                || radius == -1
                || DistanceCalculatorUtil.calculateDistance(sender, recipient) <= radius;
    }

    @Cacheable
    private Component createFinalMessage(ChatMessage chatMessage) {
        Chat chat = chatMessage.getChat();

        Component messageFormat = formatterUtil.formatMessage(chatMessage.getSender(), chat.messageFormat);
        Component hoverFormat = createHoverFormat(chat.hoverFormat, chatMessage.getSender());

        TextReplacementConfig config = TextReplacementConfig.builder()
                .matchLiteral("{message}")
                .replacement(chatMessage.getContent())
                .build();

        Component finalMessage = messageFormat.replaceText(config);
        if (hoverFormat != null) {
            finalMessage = finalMessage.hoverEvent(HoverEvent.showText(hoverFormat));
        }

        return finalMessage;
    }

    @Cacheable
    private Component createHoverFormat(List<String> hoverFormatList, Player sender) {
        if (hoverFormatList == null || hoverFormatList.isEmpty()) {
            return null;
        }

        String hoverFormatString = String.join("\n", hoverFormatList);
        return formatterUtil.formatMessage(sender, hoverFormatString);
    }
}
