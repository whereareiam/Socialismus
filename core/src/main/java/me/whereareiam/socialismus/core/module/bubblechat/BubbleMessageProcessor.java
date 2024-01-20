package me.whereareiam.socialismus.core.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.*;

@Singleton
public class BubbleMessageProcessor {
	private final LoggerUtil loggerUtil;
	private final FormatterUtil formatterUtil;
	private final MessageUtil messageUtil;
	private final BubbleChatConfig bubbleChatConfig;

	@Inject
	public BubbleMessageProcessor(LoggerUtil loggerUtil, FormatterUtil formatterUtil,
	                              MessageUtil messageUtil, BubbleChatConfig bubbleChatConfig) {
		this.loggerUtil = loggerUtil;
		this.formatterUtil = formatterUtil;
		this.messageUtil = messageUtil;
		this.bubbleChatConfig = bubbleChatConfig;

		loggerUtil.trace("Initializing class: " + this);
	}

	public Queue<BubbleMessage> processMessage(ChatMessage chatMessage, Collection<? extends Player> receivers) {
		loggerUtil.debug("Processing chat message");

		Player sender = chatMessage.getSender();
		Component message = chatMessage.getContent();

		Queue<BubbleMessage> messages = new LinkedList<>();
		List<Component> lines = splitIntoLines(message);

		boolean isFirstMessage = true;
		while (!lines.isEmpty()) {
			loggerUtil.debug("Processing lines of the message");
			List<Component> messageLines = extractLines(lines);

			Component messageComponent = joinLinesIntoComponent(messageLines);
			double displayTime = calculateDisplayTime(messageComponent);

			Component formatMessageComponent = getFormat(isFirstMessage, sender);
			isFirstMessage = false;

			formatMessageComponent = messageUtil.replacePlaceholder(formatMessageComponent, "{playerName}", sender.getName());
			formatMessageComponent = messageUtil.replacePlaceholder(formatMessageComponent, "{message}", messageComponent);

			formatMessageComponent = appendEndOrCutFormat(formatMessageComponent, lines.isEmpty(), sender);

			BubbleMessage bubbleMessage = new BubbleMessage(displayTime, formatMessageComponent, chatMessage.getSender(), receivers);
			messages.add(bubbleMessage);
		}
		return messages;
	}

	private List<Component> extractLines(List<Component> lines) {
		loggerUtil.debug("Extracting lines from the message");
		int maxLines = bubbleChatConfig.settings.lineCount + 1;

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
		int maxLineLength = calculateMaxLineLength(bubbleChatConfig.settings.lineWidth);

		List<Component> lines = new ArrayList<>();
		String[] words = PlainTextComponentSerializer.plainText().serialize(message).split(" ");
		StringBuilder line = new StringBuilder();
		for (String word : words) {
			if (line.length() + word.length() + 1 > maxLineLength) {
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
		System.out.println(lineWidth);
		return (int) Math.round(lineWidth * 16.0 / 100.0);
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
