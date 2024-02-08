package me.whereareiam.socialismus.core.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.event.chat.AfterChatSendMessageEvent;
import me.whereareiam.socialismus.api.event.chat.OnChatSendMessageEvent;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessageFormat;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.platform.PlatformIdentifier;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;

import java.util.Optional;

@Singleton
public class ChatBroadcaster {
	private final SettingsConfig settingsConfig;
	private final LoggerUtil loggerUtil;
	private final FormatterUtil formatterUtil;
	private final MessageUtil messageUtil;

	@Inject
	public ChatBroadcaster(SettingsConfig settingsConfig, LoggerUtil loggerUtil, FormatterUtil formatterUtil,
	                       MessageUtil messageUtil) {
		this.settingsConfig = settingsConfig;
		this.loggerUtil = loggerUtil;
		this.formatterUtil = formatterUtil;
		this.messageUtil = messageUtil;

		loggerUtil.trace("Initializing class: " + this);
	}

	public void broadcastMessage(ChatMessage chatMessage) {
		ChatMessageFormat chatMessageFormat = getChatMessageFormat(chatMessage);
		if (chatMessageFormat == null)
			return;

		chatMessage.setContent(createComponent(chatMessage, chatMessageFormat));

		OnChatSendMessageEvent event = new OnChatSendMessageEvent(chatMessage);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		chatMessage = event.getChatMessage();

		if (!PlatformIdentifier.isPaper() || !settingsConfig.modules.chats.useVanillaChat) {
			ChatMessage finalChatMessage = chatMessage;
			chatMessage.getRecipients().forEach(recipient -> messageUtil.sendMessage(recipient, finalChatMessage.getContent()));
			chatMessage.setCancelled(true);
		}

		if (chatMessageFormat.sound != null) {
			chatMessage.getRecipients().forEach(recipient -> recipient.playSound(recipient.getLocation(), chatMessageFormat.sound, 1, 1));
		}
		loggerUtil.info("[" + chatMessage.getChat().id.toUpperCase() + "] " + chatMessage.getSender().getName() + ": " + PlainTextComponentSerializer.plainText().serialize(chatMessage.getContent()));

		AfterChatSendMessageEvent afterEvent = new AfterChatSendMessageEvent(chatMessage);
		Bukkit.getPluginManager().callEvent(afterEvent);
	}

	private Component createComponent(ChatMessage chatMessage, ChatMessageFormat chatMessageFormat) {
		Component messageFormat = formatterUtil.formatMessage(chatMessage.getSender(), chatMessageFormat.format, true);

		messageFormat = messageUtil.replacePlaceholder(messageFormat, "{playerName}", chatMessage.getSender().getName());
		messageFormat = messageUtil.replacePlaceholder(messageFormat, "{message}", chatMessage.getContent());

		return messageFormat;
	}

	private ChatMessageFormat getChatMessageFormat(ChatMessage chatMessage) {
		Chat chat = chatMessage.getChat();
		Optional<ChatMessageFormat> format = chat.formats.stream()
				.filter(f -> f.permission.isBlank() && f.permission.isEmpty() || chatMessage.getSender().hasPermission(f.permission))
				.findFirst();

		return format.orElse(null);
	}
}
