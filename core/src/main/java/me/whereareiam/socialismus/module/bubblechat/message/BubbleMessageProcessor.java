package me.whereareiam.socialismus.module.bubblechat.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageProcessor;
import me.whereareiam.socialismus.config.module.bubblechat.BubbleChatConfig;
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

        loggerUtil.trace("Initializing class: " + this);
    }

    public Queue<BubbleMessage> processMessage(ChatMessage chatMessage, Collection<Player> receivers) {
        loggerUtil.debug("Processing chat message");
        chatMessage = processChatMessage(chatMessage);

        Player player = chatMessage.getSender();
        Component message = chatMessage.getContent();

        if (PlainTextComponentSerializer.plainText().serialize(message).length() <= bubbleChatConfig.requirements.symbolCountThreshold) {
            loggerUtil.debug("The message is too short, not creating a bubble message");
            return new LinkedList<>();
        }

        Queue<BubbleMessage> messages = new LinkedList<>();
        List<Component> lines = splitIntoLines(message);
        boolean isFirstMessage = true;
        while (!lines.isEmpty()) {
            loggerUtil.debug("Processing lines of the message");
            List<Component> messageLines = extractLines(lines);

            Component messageComponent = joinLinesIntoComponent(messageLines);
            double displayTime = calculateDisplayTime(messageComponent);

            Component formatMessageComponent = getFormat(isFirstMessage, player);
            isFirstMessage = false;

            formatMessageComponent = replaceInternalPlaceholders(player, formatMessageComponent, messageComponent);

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
        loggerUtil.debug("Extracting lines from the message");
        List<Component> messageLines = new ArrayList<>();
        for (int i = 0; i < maxLines && !lines.isEmpty(); i++) {
            messageLines.add(lines.remove(0));
        }
        return messageLines;
    }

    private Component joinLinesIntoComponent(List<Component> messageLines) {
        loggerUtil.debug("Joining lines into a component");
        JoinConfiguration joinConfig = JoinConfiguration.separator(Component.newline());
        return Component.join(joinConfig, messageLines);
    }

    private Component getFormat(boolean isFirstMessage, Player player) {
        loggerUtil.debug("Getting format for the message");
        String format;
        if (isFirstMessage) {
            format = bubbleChatConfig.format.startMessageFormat;
        } else {
            format = bubbleChatConfig.format.queueMessageFormat;
        }
        return formatterUtil.formatMessage(player, format);
    }

    private Component replaceInternalPlaceholders(Player player, Component format, Component bubbleMessageComponent) {
        loggerUtil.debug("Replacing internal placeholders");

        TextReplacementConfig playerNamePlaceholder = TextReplacementConfig.builder()
                .matchLiteral("{playerName}")
                .replacement(player.getName())
                .build();

        TextReplacementConfig messagePlaceholder = TextReplacementConfig.builder()
                .matchLiteral("{message}")
                .replacement(bubbleMessageComponent)
                .build();

        return format.replaceText(playerNamePlaceholder).replaceText(messagePlaceholder);
    }

    private Component appendEndOrCutFormat(Component format, boolean isLastLine, Player player) {
        loggerUtil.debug("Appending end or cutting format");
        if (!isLastLine) {
            format = format.append(formatterUtil.formatMessage(player, bubbleChatConfig.format.cutMessageFormat));
        } else {
            format = format.append(formatterUtil.formatMessage(player, bubbleChatConfig.format.endMessageFormat));
        }
        return format;
    }

    private List<Component> splitIntoLines(Component message) {
        loggerUtil.debug("Splitting message into lines");
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
        loggerUtil.debug("Calculating maximum line length");
        return (int) Math.round(lineWidth * 16.0 / 100);
    }

    private double calculateDisplayTime(Component message) {
        loggerUtil.debug("Calculating display time");
        int symbolCount = PlainTextComponentSerializer.plainText().serialize(message).length();
        double displayTime = (symbolCount * bubbleChatConfig.settings.timePerSymbol);

        double minimumTime = bubbleChatConfig.settings.minimumTime;

        if (displayTime <= minimumTime) {
            displayTime = minimumTime;
        }

        return displayTime;
    }
}
