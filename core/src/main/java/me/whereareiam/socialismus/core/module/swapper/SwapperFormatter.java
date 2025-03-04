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
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class SwapperFormatter {
		private final FormatterUtil formatterUtil;
		private final MessageUtil messageUtil;
		private final BubbleChatConfig bubbleChatConfig;
		private final SwapperModule swapperModule;
		private final SwapperRequirementValidator swapperRequirementValidator;

		private final Random random = new Random();

		@Inject
		public SwapperFormatter(
						FormatterUtil formatterUtil,
						MessageUtil messageUtil,
						BubbleChatConfig bubbleChatConfig,
						SwapperModule swapperModule,
						SwapperRequirementValidator swapperRequirementValidator
		) {
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
						if (!swapperRequirementValidator.validatePlayer(swapper, player, true))
								continue;

						for (String placeholder : swapper.placeholders) {
								if (placeholder.startsWith("$")) {
										String regexPattern = placeholder.substring(1);
										content = replaceRegex(content, regexPattern, swapper, player);
								} else {
										if (!content.toString().contains(placeholder))
												continue;

										Component replacement = createReplacementFromSwapper(swapper, player, null);
										content = messageUtil.replacePlaceholder(content, placeholder, replacement);
								}
						}
				}
				return content;
		}

		private Component replaceRegex(Component content, String pattern, Swapper swapper, Player player) {
				String fullText = PlainTextComponentSerializer.plainText().serialize(content);

				Pattern p = Pattern.compile(pattern);
				Matcher m = p.matcher(fullText);

				if (!m.find())
						return content;

				m.reset();
				StringBuilder builder = new StringBuilder();
				int lastEnd = 0;

				Map<String, Component> regexReplacements = new HashMap<>();

				while (m.find()) {
						builder.append(fullText, lastEnd, m.start());

						String matchedText = m.group();
						Component replacement = createReplacementFromSwapper(swapper, player, matchedText);
						String replacementMarker = "%%REPLACEMENT_" + System.nanoTime() + "%%";
						builder.append(replacementMarker);

						regexReplacements.put(replacementMarker, replacement);

						lastEnd = m.end();
				}

				builder.append(fullText.substring(lastEnd));

				return reassembleComponentWithReplacements(builder.toString(), regexReplacements);
		}

		private Component reassembleComponentWithReplacements(String str, Map<String, Component> regexReplacements) {
				TextComponent.Builder parent = Component.text();

				String remaining = str;
				while (true) {
						int nextIdx = -1;
						String foundMarker = null;

						for (String marker : regexReplacements.keySet()) {
								int idx = remaining.indexOf(marker);
								if (idx != -1 && (nextIdx == -1 || idx < nextIdx)) {
										nextIdx = idx;
										foundMarker = marker;
								}
						}

						if (nextIdx == -1) {
								parent.append(Component.text(remaining));
								break;
						}

						String textBefore = remaining.substring(0, nextIdx);
						parent.append(Component.text(textBefore));

						parent.append(regexReplacements.get(foundMarker));

						remaining = remaining.substring(nextIdx + foundMarker.length());
				}

				return parent.build();
		}

		private Component createReplacementFromSwapper(Swapper swapper, Player player, String matchedText) {
				String rawContent;
				if (swapper.settings.randomContent) {
						int randomIndex = random.nextInt(swapper.content.size());
						rawContent = swapper.content.get(randomIndex);
				} else {
						rawContent = swapper.content.getFirst();
				}

				if (matchedText != null)
						rawContent = rawContent.replace("{matched}", matchedText);

				Component replacement = formatterUtil.formatMessage(player, rawContent, true);

				if (!swapper.contentHover.isEmpty()) {
						StringBuilder hoverText = new StringBuilder();
						for (int s = 0; s < swapper.contentHover.size(); s++) {
								hoverText.append(swapper.contentHover.get(s));
								if (s != swapper.contentHover.size() - 1)
										hoverText.append("\n");
						}

						String finalHover = hoverText.toString();
						if (matchedText != null)
								finalHover = finalHover.replace("{matched}", matchedText);

						replacement = replacement.hoverEvent(
										HoverEvent.showText(formatterUtil.formatMessage(player, finalHover, true))
						);
				}

				if (swapper.settings.sound != null)
						player.playSound(
										player.getLocation(),
										swapper.settings.sound.toLowerCase(),
										swapper.settings.soundVolume,
										swapper.settings.soundPitch
						);

				return replacement;
		}
}