package me.whereareiam.socialismus.core.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.event.chat.AfterChatSendMessageEvent;
import me.whereareiam.socialismus.api.event.chat.OnChatSendMessageEvent;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessageFormat;
import me.whereareiam.socialismus.core.platform.PlatformIdentifier;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@Singleton
public class ChatBroadcaster {
	private final LoggerUtil loggerUtil;
	private final FormatterUtil formatterUtil;
	private final MessageUtil messageUtil;

	@Inject
	public ChatBroadcaster(LoggerUtil loggerUtil, FormatterUtil formatterUtil,
	                       MessageUtil messageUtil) {
		this.loggerUtil = loggerUtil;
		this.formatterUtil = formatterUtil;
		this.messageUtil = messageUtil;

		loggerUtil.trace("Initializing class: " + this);
	}

	public void broadcastMessage(ChatMessage chatMessage) {
		Component finalMessage = createFinalMessage(chatMessage);
		if (finalMessage == null)
			return;

		chatMessage.setContent(finalMessage);

		OnChatSendMessageEvent event = new OnChatSendMessageEvent(chatMessage);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		chatMessage = event.getChatMessage();

		if (!PlatformIdentifier.isPaper()) {
			ChatMessage finalChatMessage = chatMessage;
			chatMessage.getRecipients().forEach(recipient -> messageUtil.sendMessage(recipient, finalChatMessage.getContent()));
			loggerUtil.info("[" + chatMessage.getChat().id.toUpperCase() + "] " + chatMessage.getSender().getName() + ": " + PlainTextComponentSerializer.plainText().serialize(chatMessage.getContent()));
			chatMessage.setCancelled(true);
		}

		AfterChatSendMessageEvent afterEvent = new AfterChatSendMessageEvent(chatMessage);
		Bukkit.getPluginManager().callEvent(afterEvent);
	}

	private Component createFinalMessage(ChatMessage chatMessage) {
		Chat chat = chatMessage.getChat();
		Optional<ChatMessageFormat> format = chat.formats.stream()
				.filter(f -> f.permission.isBlank() && f.permission.isEmpty() || chatMessage.getSender().hasPermission(f.permission))
				.findFirst();

		if (format.isEmpty())
			return null;

		Component messageFormat = formatterUtil.formatMessage(chatMessage.getSender(), format.get().format);
		Component hoverFormat = createHoverFormat(chat.hoverFormat, chatMessage.getSender());

		messageFormat = messageUtil.replacePlaceholder(messageFormat, "{playerName}", chatMessage.getSender().getName());
		messageFormat = messageUtil.replacePlaceholder(messageFormat, "{message}", chatMessage.getContent());

		if (hoverFormat != null) {
			messageFormat = messageFormat.hoverEvent(HoverEvent.showText(hoverFormat));
		}

		return messageFormat;
	}

	private Component createHoverFormat(List<String> hoverFormatList, Player sender) {
		if (hoverFormatList == null || hoverFormatList.isEmpty()) {
			return null;
		}

		String hoverFormatString = String.join("\n", hoverFormatList);
		return formatterUtil.formatMessage(sender, hoverFormatString);
	}
}
