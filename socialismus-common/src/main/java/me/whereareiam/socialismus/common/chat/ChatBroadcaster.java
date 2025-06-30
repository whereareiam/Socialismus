package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.model.CommandEntity;
import me.whereareiam.socialismus.api.model.chat.ChatMessages;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatBroadcaster {
	private final PlayerContainerService playerContainer;

	private final Provider<ChatSettings> chatSettings;
	private final Provider<ChatMessages> chatMessages;
	private final Provider<Map<String, CommandEntity>> commands;

	public void broadcast(FormattedChatMessage chatMessage) {
		Logger.info("[%s] %s: %s", chatMessage.getChat().getId().toUpperCase(), chatMessage.getSender().getUsername(), ComponentUtil.toLegacy(chatMessage.getContent(), true));

		chatMessage.getRecipients().forEach(recipient ->
				recipient.sendMessage(
						chatMessage.getFormat()
								.replaceText(createMessageReplacement(chatMessage.getContent()))
								.replaceText(createClearReplacement(chatMessage, recipient.getUniqueId()))
				)
		);
	}

	public TextReplacementConfig createMessageReplacement(Component component) {
		return TextReplacementConfig.builder()
				.matchLiteral("{message}")
				.replacement(component)
				.build();
	}

	public TextReplacementConfig createClearReplacement(FormattedChatMessage formattedChatMessage, UUID recipientUniqueId) {
		DummyPlayer sender = formattedChatMessage.getSender();
		Optional<DummyPlayer> recipient = playerContainer.getPlayer(recipientUniqueId);

		if (recipient.isPresent()
				&& recipient.get().hasPermission(chatSettings.get().getHistory().getPermission())
				&& !sender.hasPermission(chatSettings.get().getHistory().getBypassPermission())) {
			return TextReplacementConfig.builder()
					.matchLiteral("{clear}")
					.replacement(Serializer.serialize(sender, chatMessages.get().getClearFormat().getFormat())
							.clickEvent(ClickEvent.runCommand(
									"/" + commands.get().get("clear").getUsage()
											.replace("{command}", commands.get().get("main").getAliases().get(0))
											.replace("{alias}", commands.get().get("clear").getAliases().get(0))
											.replace("[context]", String.valueOf(formattedChatMessage.getId()))))
					)
					.build();
		}

		return TextReplacementConfig.builder()
				.matchLiteral("{clear}")
				.replacement(Component.empty())
				.build();
	}
}
