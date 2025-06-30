package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.input.chat.ChatCoordinationService;
import me.whereareiam.socialismus.api.input.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.api.input.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.util.EventUtil;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcastPolicy;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.chat.processor.FormattedChatMessageProcessor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatCoordinator implements ChatCoordinationService {
	private final FormattedChatMessageProcessor formattedChatMessageProcessor;
	private final ChatHistoryContainerService chatHistoryContainer;
	private final ChatMessageProcessor chatMessageProcessor;
	private final ChatBroadcaster chatBroadcaster;
	private final ChatBroadcastPolicy policy;

	public FormattedChatMessage coordinate(ChatMessage chatMessage) {
		chatMessage = chatMessageProcessor.process(chatMessage);
		if (chatMessage.isCancelled()) return FormattedChatMessage.builder().cancelled(true).build();

		FormattedChatMessage formattedChatMessage = formattedChatMessageProcessor.process(chatMessage);

		EventUtil.callEvent(new ChatBroadcastEvent(formattedChatMessage, formattedChatMessage.isCancelled()), () -> {
			formattedChatMessage.getSender().setLastChat(formattedChatMessage.getChat());
			if (policy.allows(formattedChatMessage))
				chatBroadcaster.broadcast(formattedChatMessage);

			chatHistoryContainer.addMessage(formattedChatMessage.getId(), formattedChatMessage);
		});

		return formattedChatMessage;
	}
}
