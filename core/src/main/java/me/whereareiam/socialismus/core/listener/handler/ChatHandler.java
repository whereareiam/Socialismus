package me.whereareiam.socialismus.core.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.ChatMessage;
import me.whereareiam.socialismus.core.chat.ChatService;
import me.whereareiam.socialismus.core.chat.message.ChatMessageFactory;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

@Singleton
public class ChatHandler {
	private final Injector injector;
	private final ChatMessageFactory chatMessageFactory;
	private final SettingsConfig settingsConfig;

	@Inject
	public ChatHandler(Injector injector, LoggerUtil loggerUtil, ChatMessageFactory chatMessageFactory, SettingsConfig settingsConfig) {
		this.injector = injector;
		this.chatMessageFactory = chatMessageFactory;
		this.settingsConfig = settingsConfig;

		loggerUtil.trace("Initializing class: " + this);
	}

	public ChatMessage handleChatEvent(Player player, Collection<? extends Player> recipients, String message) {
		ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, recipients, message, Optional.empty());

		if (settingsConfig.modules.chats && chatMessage.getChat() != null) {
			final ChatService chatService = injector.getInstance(ChatService.class);
			chatService.distributeMessage(chatMessage);
		}

		return chatMessage;
	}
}

