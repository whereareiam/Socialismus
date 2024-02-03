package me.whereareiam.socialismus.core.module.swapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.swapper.Swapper;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

@Singleton
public class SwapperFormatter {
	private final FormatterUtil formatterUtil;
	private final MessageUtil messageUtil;
	private final BubbleChatConfig bubbleChatConfig;
	private final SwapperModule swapperModule;
	private final SwapperRequirementValidator swapperRequirementValidator;

	private final Random random = new Random();

	@Inject
	public SwapperFormatter(FormatterUtil formatterUtil, MessageUtil messageUtil, BubbleChatConfig bubbleChatConfig,
	                        SwapperModule swapperModule, SwapperRequirementValidator swapperRequirementValidator) {
		this.formatterUtil = formatterUtil;
		this.messageUtil = messageUtil;
		this.bubbleChatConfig = bubbleChatConfig;
		this.swapperModule = swapperModule;
		this.swapperRequirementValidator = swapperRequirementValidator;
	}


	public BubbleMessage hookSwapper(BubbleMessage bubbleMessage) {
		if (!bubbleChatConfig.settings.enableSwapper)
			return bubbleMessage;

		Component component = hookSwapper(bubbleMessage.getContent(), bubbleMessage.getSender());
		bubbleMessage.setContent(component);

		return bubbleMessage;
	}

	public ChatMessage hookSwapper(ChatMessage chatMessage) {
		if (!chatMessage.getChat().enableSwapper)
			return chatMessage;

		Component component = hookSwapper(chatMessage.getContent(), chatMessage.getSender());
		chatMessage.setContent(component);

		return chatMessage;
	}

	private Component hookSwapper(Component content, Player player) {
		List<Swapper> swappers = swapperModule.getSwappers();
		for (Swapper swapper : swappers) {
			for (int i = 0; i < swapper.placeholders.size(); i++) {
				String placeholder = swapper.placeholders.get(i);
				if (!content.toString().contains(placeholder)) {
					continue;
				}

				if (!swapperRequirementValidator.validatePlayer(swapper, player, true)) {
					return content;
				}

				Component replacement;
				if (swapper.settings.randomContent) {
					int randomIndex = random.nextInt(swapper.content.size());
					replacement = formatterUtil.formatMessage(player, swapper.content.get(randomIndex), true);
				} else {
					replacement = formatterUtil.formatMessage(player, swapper.content.get(0), true);
				}

				if (!swapper.contentHover.isEmpty()) {
					StringBuilder hoverText = new StringBuilder();
					for (int s = 0; s < swapper.contentHover.size(); s++) {
						hoverText.append(swapper.contentHover.get(s));
						if (s != swapper.contentHover.size() - 1) {
							hoverText.append("\n");
						}
					}
					replacement = replacement.hoverEvent(HoverEvent.showText(formatterUtil.formatMessage(player, hoverText.toString(), true)));
				}

				if (swapper.settings.sound != null) {
					player.playSound(player.getLocation(), swapper.settings.sound, swapper.settings.soundVolume, swapper.settings.soundPitch);
				}

				content = messageUtil.replacePlaceholder(content, placeholder, replacement);
			}
		}
		return content;
	}
}
