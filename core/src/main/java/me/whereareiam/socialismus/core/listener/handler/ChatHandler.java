package me.whereareiam.socialismus.core.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.chat.ChatService;
import me.whereareiam.socialismus.core.chat.message.ChatMessage;
import me.whereareiam.socialismus.core.chat.message.ChatMessageFactory;
import me.whereareiam.socialismus.core.config.setting.SettingsConfig;
import me.whereareiam.socialismus.core.integration.IntegrationManager;
import me.whereareiam.socialismus.core.module.bubblechat.BubbleChatService;
import me.whereareiam.socialismus.core.module.bubblechat.BubbleTriggerType;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import org.bukkit.entity.Player;

import java.util.Optional;

@Singleton
public class ChatHandler {
	private final Injector injector;
	private final LoggerUtil loggerUtil;
	private final IntegrationManager integrationManager;
	private final ChatMessageFactory chatMessageFactory;
	private final SettingsConfig settingsConfig;

	@Inject
	public ChatHandler(Injector injector, LoggerUtil loggerUtil, IntegrationManager integrationManager,
					   ChatMessageFactory chatMessageFactory, SettingsConfig settingsConfig) {
		this.injector = injector;
		this.loggerUtil = loggerUtil;
		this.integrationManager = integrationManager;
		this.chatMessageFactory = chatMessageFactory;
		this.settingsConfig = settingsConfig;

		loggerUtil.trace("Initializing class: " + this);
	}

	public boolean handleChatEvent(Player player, String message) {
		boolean cancelEvent = false;
		ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, message, Optional.empty());

		if (integrationManager.isIntegrationEnabled("ProtocolLib")) {
			final BubbleChatService bubbleChatService = injector.getInstance(BubbleChatService.class);
			if (settingsConfig.modules.bubblechat)
				bubbleChatService.distributeBubbleMessage(BubbleTriggerType.CHAT, chatMessage);
		} else {
			loggerUtil.warning("You can't use the BubbleChat module without ProtocolLib!");
		}

		if (settingsConfig.modules.chats && chatMessage.getChat() != null) {
			cancelEvent = true;

			final ChatService chatService = injector.getInstance(ChatService.class);
			chatService.distributeMessage(chatMessage);
		}

		return cancelEvent;
	}
}

