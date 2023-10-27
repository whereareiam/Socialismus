package me.whereareiam.socialismus.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageProcessor;
import me.whereareiam.socialismus.chat.model.Chat;
import me.whereareiam.socialismus.util.DistanceCalculatorUtil;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class ChatBroadcaster {
    private final LoggerUtil loggerUtil;
    private final MessageUtil messageUtil;
    private final FormatterUtil formatterUtil;

    private final Set<ChatMessageProcessor> chatMessageProcessors;

    @Inject
    public ChatBroadcaster(LoggerUtil loggerUtil, MessageUtil messageUtil, FormatterUtil formatterUtil,
                           Set<ChatMessageProcessor> chatMessageProcessors) {
        this.loggerUtil = loggerUtil;
        this.messageUtil = messageUtil;
        this.formatterUtil = formatterUtil;

        this.chatMessageProcessors = new HashSet<>(chatMessageProcessors);
    }

    public void broadcastMessage(ChatMessage chatMessage, Player recipient) {
        if (shouldSendMessage(chatMessage.getSender(), recipient, chatMessage.getChat().requirements.radius)) {
            Component finalMessage = createFinalMessage(chatMessage);
            messageUtil.sendMessage(recipient, finalMessage);

            loggerUtil.trace("Sent message: " + finalMessage + " to: " + recipient.getName());
        }
    }

    private boolean shouldSendMessage(Player sender, Player recipient, int radius) {
        return sender.equals(recipient)
                || radius == -1
                || DistanceCalculatorUtil.calculateDistance(sender, recipient) <= radius;
    }

    private Component createFinalMessage(ChatMessage chatMessage) {
        for (ChatMessageProcessor processor : chatMessageProcessors) {
            chatMessage = processor.process(chatMessage);
        }

        Chat chat = chatMessage.getChat();
        Component messageFormat = formatterUtil.formatMessage(chatMessage.getSender(), chat.messageFormat);
        Component hoverFormat = createHoverFormat(chat.hoverFormat, chatMessage.getSender());

        TextReplacementConfig playerNameConfig = TextReplacementConfig.builder()
                .matchLiteral("{playerName}")
                .replacement(chatMessage.getSender().getName())
                .build();

        TextReplacementConfig messageConfig = TextReplacementConfig.builder()
                .matchLiteral("{message}")
                .replacement(chatMessage.getContent())
                .build();

        Component finalMessage = messageFormat.replaceText(playerNameConfig).replaceText(messageConfig);
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
