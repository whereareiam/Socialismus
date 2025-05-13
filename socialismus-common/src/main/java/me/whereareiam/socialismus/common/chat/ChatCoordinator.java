package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.chat.ChatCoordinationService;
import me.whereareiam.socialismus.api.input.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.api.input.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.type.PlatformType;
import me.whereareiam.socialismus.api.util.EventUtil;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.chat.processor.FormattedChatMessageProcessor;

@Singleton
public class ChatCoordinator implements ChatCoordinationService {
	private final ChatMessageProcessor chatMessageProcessor;
	private final FormattedChatMessageProcessor formattedChatMessageProcessor;
	private final ChatBroadcaster chatBroadcaster;
	private final ChatHistoryContainerService chatHistoryContainer;

	@Inject
	public ChatCoordinator(ChatMessageProcessor chatMessageProcessor, FormattedChatMessageProcessor formattedChatMessageProcessor,
	                       ChatBroadcaster chatBroadcaster, ChatHistoryContainerService chatHistoryContainer) {
		this.chatMessageProcessor = chatMessageProcessor;
		this.formattedChatMessageProcessor = formattedChatMessageProcessor;
		this.chatBroadcaster = chatBroadcaster;
		this.chatHistoryContainer = chatHistoryContainer;
	}

	public FormattedChatMessage coordinate(ChatMessage chatMessage) {
		chatMessage = chatMessageProcessor.process(chatMessage);
		if (chatMessage.isCancelled()) return FormattedChatMessage.builder().cancelled(true).build();

		FormattedChatMessage formattedChatMessage = formattedChatMessageProcessor.process(chatMessage);

		EventUtil.callEvent(new ChatBroadcastEvent(formattedChatMessage, formattedChatMessage.isCancelled()), () -> {
			formattedChatMessage.getSender().setLastChat(formattedChatMessage.getChat());
			if (!formattedChatMessage.isVanillaSending() || PlatformType.isProxy())
				chatBroadcaster.broadcast(formattedChatMessage);

			chatHistoryContainer.addMessage(formattedChatMessage.getId(), formattedChatMessage);
		});

		return formattedChatMessage;
	}
}
