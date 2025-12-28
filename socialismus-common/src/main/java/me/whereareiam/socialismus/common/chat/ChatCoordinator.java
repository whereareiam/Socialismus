package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcastPolicy;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.chat.processor.FormattedChatMessageProcessor;
import me.whereareiam.socialismus.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.service.chat.ChatCoordinationService;
import me.whereareiam.socialismus.service.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.util.EventUtil;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatCoordinator implements ChatCoordinationService {
	private final FormattedChatMessageProcessor formattedChatMessageProcessor;
	private final ChatHistoryContainerService chatHistoryContainer;
	private final ChatMessageProcessor chatMessageProcessor;
	private final ChatBroadcaster chatBroadcaster;
	private final ChatBroadcastPolicy policy;

	public FormattedChatMessage coordinate(ChatMessage raw) {
		raw = chatMessageProcessor.process(raw);
		if (raw.isCancelled()) return FormattedChatMessage.builder().cancelled(true).build();

		FormattedChatMessage formatted = formattedChatMessageProcessor.process(raw);
		broadcastAndStore(formatted);
		return formatted;
	}

	public void coordinate(FormattedChatMessage formatted) {
		if (formatted.isCancelled()) return;

		FormattedChatMessage processed = (FormattedChatMessage) chatMessageProcessor.process(formatted);
		processed.setFormat(formatted.getFormat());

		if (processed.isCancelled()) return;
		broadcastAndStore(processed);
	}

	private void broadcastAndStore(FormattedChatMessage msg) {
		EventUtil.callEvent(
				new ChatBroadcastEvent(msg, msg.isCancelled()),
				() -> {
					msg.getSender().setData(Constants.DataKeys.LAST_CHAT, msg.getChat());
					msg.getSender().setData(Constants.DataKeys.LAST_TRIGGER, msg.getTrigger());
					if (policy.allows(msg))
						chatBroadcaster.broadcast(msg);
					chatHistoryContainer.addMessage(msg.getId(), msg);
				}
		);
	}
}
