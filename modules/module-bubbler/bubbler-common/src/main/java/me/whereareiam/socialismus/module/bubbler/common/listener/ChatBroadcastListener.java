package me.whereareiam.socialismus.module.bubbler.common.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.EventOrder;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.module.bubbler.api.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatBroadcastListener implements EventListener {
	private final BubbleCoordinationService coordinationService;

	@SocialisticEvent(EventOrder.NORMAL)
	public void onChatBroadcast(ChatBroadcastEvent event) {
		if (event.getChatMessage().getOrigin() != null) return;

		coordinationService.coordinate(BubbleMessage.builder()
				.sender(event.getChatMessage().getSender())
				.recipients(event.getChatMessage().getRecipients())
				.content(event.getChatMessage().getContent())
				.activatorType(ActivatorType.CHAT)
				.build()
		);
	}
}
