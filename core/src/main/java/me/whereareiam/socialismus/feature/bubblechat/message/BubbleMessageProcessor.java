package me.whereareiam.socialismus.feature.bubblechat.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageProcessor;
import me.whereareiam.socialismus.config.feature.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.*;

@Singleton
public class BubbleMessageProcessor {
    private final LoggerUtil loggerUtil;
    private final FormatterUtil formatterUtil;
    private final BubbleChatConfig bubbleChatConfig;
    private final int maxLines;
    private final int maxLineLength;

    private final Set<ChatMessageProcessor> chatMessageProcessors;

    @Inject
    public BubbleMessageProcessor(LoggerUtil loggerUtil, FormatterUtil formatterUtil,
                                  BubbleChatConfig bubbleChatConfig,
                                  Set<ChatMessageProcessor> chatMessageProcessors) {
        this.loggerUtil = loggerUtil;
        this.formatterUtil = formatterUtil;
        this.bubbleChatConfig = bubbleChatConfig;
        this.maxLines = bubbleChatConfig.settings.lineCount + 1;
        this.maxLineLength = calculateMaxLineLength(bubbleChatConfig.settings.lineWidth);

        this.chatMessageProcessors = new HashSet<>(chatMessageProcessors);
    }

    public Queue<BubbleMessage> processMessage(ChatMessage chatMessage, Collection<Player> receivers) {
        chatMessage = processChatMessage(chatMessage);

        Player player = chatMessage.getSender();
        Component message = chatMessage.getContent();

        Queue<BubbleMessage> messages = new LinkedList<>();
        List<Component> lines = splitIntoLines(message);
        boolean isFirstMessage = true;
        while (!lines.isEmpty()) {
            List<Component> messageLines = extractLines(lines);

            Component messageComponent = joinLinesIntoComponent(messageLines);
            double displayTime = calculateDisplayTime(messageComponent);

            Component formatMessageComponent = getFormat(isFirstMessage, player);
            isFirstMessage = false;

            formatMessageComponent = replacePlaceholderWithMessage(formatMessageComponent, messageComponent);

            formatMessageComponent = appendEndOrCutFormat(formatMessageComponent, lines.isEmpty(), player);

            BubbleMessage bubbleMessage = new BubbleMessage(displayTime, formatMessageComponent, chatMessage.getSender(), receivers);
            messages.add(bubbleMessage);
        }
        return messages;
    }

    private ChatMessage processChatMessage(ChatMessage chatMessage) {
        for (ChatMessageProcessor processor : chatMessageProcessors) {
            chatMessage = processor.process(chatMessage);
        }
        return chatMessage;
    }

    private List<Component> extractLines(List<Component> lines) {
        List<Component> messageLines = new ArrayList<>();
        for (int i = 0; i < maxLines && !lines.isEmpty(); i++) {
            messageLines.add(lines.remove(0));
        }
        return messageLines;
    }

    private Component joinLinesIntoComponent(List<Component> messageLines) {
        JoinConfiguration joinConfig = JoinConfiguration.separator(Component.newline());
        return Component.join(joinConfig, messageLines);
    }

    private Component getFormat(boolean isFirstMessage, Player player) {
        String format;
        if (isFirstMessage) {
            format = bubbleChatConfig.format.startMessageFormat;
        } else {
            format = bubbleChatConfig.format.queueMessageFormat;
        }
        return formatterUtil.formatMessage(player, format);
    }

    private Component replacePlaceholderWithMessage(Component format, Component bubbleMessageComponent) {
        TextReplacementConfig config = TextReplacementConfig.builder()
                .matchLiteral("{message}")
                .replacement(bubbleMessageComponent)
                .build();
        return format.replaceText(config);
    }

    private Component appendEndOrCutFormat(Component format, boolean isLastLine, Player player) {
        if (!isLastLine) {
            format = format.append(formatterUtil.formatMessage(player, bubbleChatConfig.format.cutMessageFormat));
        } else {
            format = format.append(formatterUtil.formatMessage(player, bubbleChatConfig.format.endMessageFormat));
        }
        return format;
    }

    private List<Component> splitIntoLines(Component message) {
        List<Component> lines = new ArrayList<>();
        String[] words = PlainTextComponentSerializer.plainText().serialize(message).split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (line.length() + word.length() > maxLineLength) {
                lines.add(Component.text(line.toString()));
                line.setLength(0);
            }
            if (word.length() > maxLineLength) {
                while (word.length() > maxLineLength) {
                    lines.add(Component.text(word.substring(0, maxLineLength)));
                    word = word.substring(maxLineLength);
                }
            }
            if (!line.isEmpty()) {
                line.append(" ");
            }
            line.append(word);
        }
        if (!line.isEmpty()) {
            lines.add(Component.text(line.toString()));
        }
        return lines;
    }

    private int calculateMaxLineLength(int lineWidth) {
        return (int) Math.round(lineWidth * 16.0 / 100);
    }

    private double calculateDisplayTime(Component message) {
        int symbolCount = PlainTextComponentSerializer.plainText().serialize(message).length();
        double displayTime = (symbolCount * bubbleChatConfig.settings.timePerSymbol);

        // Get the minimum time from your config
        double minimumTime = bubbleChatConfig.settings.minimumTime;

        if (displayTime <= minimumTime) {
            displayTime = minimumTime;
        }

        return displayTime;
    }

}
