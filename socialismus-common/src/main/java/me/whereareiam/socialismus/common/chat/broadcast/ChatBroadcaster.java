package me.whereareiam.socialismus.common.chat.broadcast;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.output.PlatformInteractor;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.type.BroadcastTarget;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;

import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatBroadcaster {
	private final PlayerRegistry playerRegistry;
	private final PlatformInteractor platformInteractor;

	private final Provider<ChatSettings> chatSettings;
	private final Provider<ChatMessages> chatMessages;
	private final Provider<Commands> commands;

	public void broadcast(FormattedChatMessage chatMessage) {
		platformInteractor.broadcast(
				chatMessage.getFormat()
						.replaceText(createMessageReplacement(chatMessage.getContent()))
						.replaceText(createClearReplacement(chatMessage, chatMessage.getSender().getUniqueId())),
				BroadcastTarget.CONSOLE
		);

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
		SocialismusPlayer sender = formattedChatMessage.getSender();
		Optional<SocialismusPlayer> recipient = playerRegistry.getPlayerData(recipientUniqueId);

		if (recipient.isPresent()
				&& recipient.get().hasPermission(chatSettings.get().getHistory().getPermission())
				&& !sender.hasPermission(chatSettings.get().getHistory().getBypassPermission())) {
			return TextReplacementConfig.builder()
					.matchLiteral("{clear}")
					.replacement(Serializer.serialize(sender, chatMessages.get().getClearFormat().getFormat())
							.clickEvent(ClickEvent.runCommand(
									"/" + commands.get().getCommands().get("clear").getUsage()
											.replace("{command}", commands.get().getCommands().get("main").getAliases().get(0))
											.replace("{alias}", commands.get().getCommands().get("clear").getAliases().get(0))
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
