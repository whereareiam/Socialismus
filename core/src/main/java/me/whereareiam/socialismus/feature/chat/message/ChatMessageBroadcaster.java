package me.whereareiam.socialismus.feature.chat.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.chat.Chat;
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
        if (shouldSendMessage(chatMessage.sender(), recipient, chatMessage.chat().radius)) {
            Audience recipientAudience = (Audience) recipient;
            Component finalMessage = createFinalMessage(chatMessage);
            recipientAudience.sendMessage(finalMessage);

            loggerUtil.trace("Sent message to player: " + recipient.getName());
        }
    }

    private boolean shouldSendMessage(Player sender, Player recipient, int radius) {
        return sender.equals(recipient) || DistanceCalculatorUtil.calculateDistance(sender, recipient) <= radius;
    }

    private Component createFinalMessage(ChatMessage chatMessage) {
        Chat chat = chatMessage.chat();

        Component messageFormat = formatterUtil.formatMessage(chatMessage.sender(), chat.messageFormat);
        Component hoverFormat = createHoverFormat(chat.hoverFormat, chatMessage.sender());

        TextReplacementConfig config = TextReplacementConfig.builder()
                .matchLiteral("{message}")
                .replacement(chatMessage.content())
                .build();

        Component finalMessage = messageFormat.replaceText(config);
        if (hoverFormat != null) {
            finalMessage = finalMessage.hoverEvent(HoverEvent.showText(hoverFormat));
        }

        return finalMessage;
    }

    private Component createHoverFormat(List<String> hoverFormatList, Player sender) {
        if (hoverFormatList == null || hoverFormatList.isEmpty()) {
            return null;
        }

        String hoverFormatString = String.join("\n", hoverFormatList);
        return formatterUtil.formatMessage(sender, hoverFormatString);
    }
}
