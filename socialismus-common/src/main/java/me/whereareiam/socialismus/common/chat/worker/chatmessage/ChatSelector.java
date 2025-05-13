package me.whereareiam.socialismus.common.chat.worker.chatmessage;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.api.input.WorkerProcessor;
import me.whereareiam.socialismus.api.input.container.ChatContainerService;
import me.whereareiam.socialismus.api.input.event.chat.ChatResolvedEvent;
import me.whereareiam.socialismus.api.model.Worker;
import me.whereareiam.socialismus.api.model.chat.ChatMessages;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.InternalChat;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.type.chat.Participants;
import me.whereareiam.socialismus.api.util.ComponentUtil;
import me.whereareiam.socialismus.api.util.EventUtil;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class ChatSelector {
	private final ChatContainerService containerService;
	private final RequirementEvaluator requirementEvaluator;

	// Configs
	private final Provider<ChatMessages> chatMessages;
	private final Provider<ChatSettings> chatSettings;

	@Inject
	public ChatSelector(
			ChatContainerService containerService,
			RequirementEvaluator requirementEvaluator,
			Provider<ChatMessages> chatMessages,
			Provider<ChatSettings> chatSettings,
			WorkerProcessor<ChatMessage> workerProcessor
	) {
		this.containerService = containerService;
		this.requirementEvaluator = requirementEvaluator;
		this.chatMessages = chatMessages;
		this.chatSettings = chatSettings;

		workerProcessor.addWorker(new Worker<>(this::selectChat, 50, true, false));
	}

	public ChatMessage selectChat(ChatMessage chatMessage) {
		Logger.debug("Selecting chat for user " + chatMessage.getSender().getUsername());
		String symbol = selectSymbol(chatMessage);

		List<InternalChat> chats = containerService.getChatBySymbol(symbol);
		if (chats.isEmpty()) {
			notifyAboutAbsentChat(chatMessage);
			chatMessage.setCancelled(true);
			return chatMessage;
		}

		InternalChat chat = chats.getFirst();
		if (chat == null || !checkRequirements(chat, chatMessage)) {
			notifyAboutAbsentChat(chatMessage);
			chatMessage.setCancelled(true);
			return chatMessage;
		}

		ChatResolvedEvent event = new ChatResolvedEvent(chatMessage, chat, chatMessage.isCancelled());
		EventUtil.callEvent(event, () -> chatMessage.setChat(event.getChat()));
		Logger.debug("Selected chat: " + chat);

		return chatMessage;
	}

	private String selectSymbol(ChatMessage chatMessage) {
		String symbol = String.valueOf(ComponentUtil.toPlain(chatMessage.getContent()).charAt(0));

		if (!containerService.hasChatBySymbol(symbol)) return "";

		chatMessage.setContent(chatMessage.getContent().replaceText(
				TextReplacementConfig.builder()
						.matchLiteral(symbol)
						.replacement("")
						.once()
						.build()
		));

		Logger.debug("Selected chat symbol: " + symbol);

		return symbol;
	}

	private boolean checkRequirements(InternalChat chat, ChatMessage chatMessage) {
		if (chat.getRequirements().get(Participants.SENDER) == null) return true;
		if (!requirementEvaluator.check(chat.getRequirements().get(Participants.SENDER), chatMessage.getSender()))
			chat = getAlternativeChat(chat, chatMessage);

		return chat != null;
	}

	private InternalChat getAlternativeChat(InternalChat chat, ChatMessage chatMessage) {
		List<InternalChat> chats = new ArrayList<>(containerService.getChatBySymbol(selectSymbol(chatMessage)));

		chats.remove(chat);
		chats.removeIf(c -> c.getId().equals(chatSettings.get().getFallback().getChatId()));

		for (InternalChat alternativeChat : chats)
			if (checkRequirements(alternativeChat, chatMessage))
				return alternativeChat;

		return getFallbackChat(chatMessage);
	}

	private InternalChat getFallbackChat(ChatMessage chatMessage) {
		ChatSettings.FallbackChatSettings fallback = chatSettings.get().getFallback();

		if (!fallback.isEnabled()) return null;

		Optional<InternalChat> fallbackChat = containerService.getChat(fallback.getChatId());
		if (fallbackChat.isEmpty()) {
			Logger.warn("Fallback chat is not available");
			notifyPlayerFallbackNotSet(chatMessage);
			chatMessage.setCancelled(true);

			return null;
		}

		return fallbackChat.get();
	}

	private void notifyPlayerFallbackNotSet(ChatMessage chatMessage) {
		if (!chatSettings.get().isNotifyNoChat()) return;

		chatMessage.getSender().sendMessage(
				Serializer.serialize(chatMessage.getSender(), chatMessages.get().getNoFallbackChat())
		);
	}

	private void notifyAboutAbsentChat(ChatMessage chatMessage) {
		if (!chatSettings.get().isNotifyNoChat()) return;

		chatMessage.getSender().sendMessage(
				Serializer.serialize(chatMessage.getSender(), chatMessages.get().getNoChatMatch())
		);
	}
}
