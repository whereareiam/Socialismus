package me.whereareiam.socialismus.core.listener.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.event.chat.BeforeChatSendMessageEvent;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.core.module.chatmention.ChatMentionService;
import me.whereareiam.socialismus.core.module.swapper.SwapperService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Singleton
public class BeforeChatSendMessageListener implements Listener {
	private final ChatMentionService chatMentionService;
	private final SwapperService swapperService;

	@Inject
	public BeforeChatSendMessageListener(ChatMentionService chatMentionService, SwapperService swapperService) {
		this.chatMentionService = chatMentionService;
		this.swapperService = swapperService;
	}

	@EventHandler
	public void onEvent(BeforeChatSendMessageEvent event) {
		ChatMessage chatMessage = event.getChatMessage();

		event.setChatMessage(chatMentionService.hookChatMention(chatMessage, event.getRecipients()));
		event.setChatMessage(swapperService.hookSwapper(chatMessage));
	}
}
