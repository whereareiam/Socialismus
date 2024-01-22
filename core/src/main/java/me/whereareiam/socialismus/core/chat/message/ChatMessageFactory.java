package me.whereareiam.socialismus.core.chat.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.type.ChatUseType;
import me.whereareiam.socialismus.core.module.chat.ChatModule;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

@Singleton
public class ChatMessageFactory {
	private final LoggerUtil loggerUtil;
	private final ChatModule chatModule;

	@Inject
	public ChatMessageFactory(LoggerUtil loggerUtil, ChatModule chatModule) {
		this.chatModule = chatModule;
		this.loggerUtil = loggerUtil;

		loggerUtil.trace("Initializing class: " + this);
	}

	public ChatMessage createChatMessage(Player sender, Collection<? extends Player> recipients, String message, Optional<String> command) {
		char chatSymbol = message.charAt(0);
		String symbol;
		Chat chat = null;

		if (command.isPresent()) {
			chat = chatModule.getChatByCommand(command.get());
			if (chat.usage.type.equals(ChatUseType.SYMBOL))
				chat = null;
		}

		if (chat == null) {
			symbol = String.valueOf(chatSymbol);
			chat = chatModule.getChatBySymbol(symbol);
			if (chat != null) {
				message = message.substring(1);
			} else {
				chat = chatModule.getChatBySymbol("");
			}
		}

		Component content = PlainTextComponentSerializer.plainText().deserialize(message.trim());

		ChatMessage chatMessage = new ChatMessage(content, chat, sender, recipients);

		loggerUtil.debug(" "
				+ "\n Created new ChatMessage "
				+ "\n chat: " + chat.id
				+ "\n content: " + PlainTextComponentSerializer.plainText().serialize(content)
				+ "\n recipients: " + recipients.stream().map(Player::getName).toList()
				+ "\n sender: " + sender.getName()
				+ " "
		);

		return chatMessage;
	}
}
