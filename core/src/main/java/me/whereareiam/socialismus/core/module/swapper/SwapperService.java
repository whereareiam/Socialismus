package me.whereareiam.socialismus.core.module.swapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.swapper.Swapper;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Random;

@Singleton
public class SwapperService implements Listener {
	private final LoggerUtil loggerUtil;
	private final SwapperModule swapperModule;
	private final FormatterUtil formatterUtil;
	private final MessageUtil messageUtil;

	private final BubbleChatConfig bubbleChatConfig;
	private final SwapperRequirementValidator swapperRequirementValidator;

	private final Random random = new Random();

	@Inject
	public SwapperService(LoggerUtil loggerUtil, SwapperModule swapperModule, FormatterUtil formatterUtil,
						  MessageUtil messageUtil, BubbleChatConfig bubbleChatConfig,
						  SwapperRequirementValidator swapperRequirementValidator) {
		this.loggerUtil = loggerUtil;
		this.swapperModule = swapperModule;
		this.formatterUtil = formatterUtil;
		this.messageUtil = messageUtil;
		this.bubbleChatConfig = bubbleChatConfig;

		this.swapperRequirementValidator = swapperRequirementValidator;

		loggerUtil.trace("Initializing class: " + this);
	}

	public BubbleMessage swapPlaceholders(BubbleMessage bubbleMessage) {
		if (! bubbleChatConfig.settings.enableSwapper)
			return bubbleMessage;

		Component component = swapPlaceholders(bubbleMessage.getContent(), bubbleMessage.getSender());
		bubbleMessage.setContent(component);

		return bubbleMessage;
	}

	public ChatMessage swapPlaceholders(ChatMessage chatMessage) {
		if (! chatMessage.getChat().enableSwapper)
			return chatMessage;

		Component component = swapPlaceholders(chatMessage.getContent(), chatMessage.getSender());
		chatMessage.setContent(component);

		return chatMessage;
	}

	private Component swapPlaceholders(Component content, Player player) {
		loggerUtil.debug("Swapping message: " + content);

		List<Swapper> swappers = swapperModule.getSwappers();
		for (Swapper swapper : swappers) {
			for (int i = 0 ; i < swapper.placeholders.size() ; i++) {
				String placeholder = swapper.placeholders.get(i);
				if (! content.toString().contains(placeholder)) {
					continue;
				}

				if (! swapperRequirementValidator.validatePlayer(swapper, player, true)) {
					return content;
				}

				Component replacement;
				if (swapper.settings.randomContent) {
					int randomIndex = random.nextInt(swapper.content.size());
					replacement = formatterUtil.formatMessage(player, swapper.content.get(randomIndex));
				} else {
					replacement = formatterUtil.formatMessage(player, swapper.content.get(0));
				}

				if (! swapper.contentHover.isEmpty()) {
					StringBuilder hoverText = new StringBuilder();
					for (int s = 0 ; s < swapper.contentHover.size() ; s++) {
						hoverText.append(swapper.contentHover.get(s));
						if (s != swapper.contentHover.size() - 1) {
							hoverText.append("\n");
						}
					}
					replacement = replacement.hoverEvent(HoverEvent.showText(formatterUtil.formatMessage(player, hoverText.toString())));
				}

				content = messageUtil.replacePlaceholder(content, placeholder, replacement);
			}
		}
		return content;
	}
}
