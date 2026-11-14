package me.whereareiam.socialismus.module.mentionator.common.notify;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.EventOrder;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.Tagging;

/**
 * Notifies the players a message tags once nothing keeps the message from going out. A message of a player hosted on
 * another server notifies nobody here: the server it was written on hands each tagged player to their own server.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TagDispatch implements EventListener {
	private final Notifier notifier;

	@SocialisticEvent(EventOrder.HIGHEST)
	public void onBroadcast(ChatBroadcastEvent event) {
		FormattedChatMessage message = event.getChatMessage();
		if (event.isCancelled() || message == null || message.isCancelled() || !message.getSender().isHostedHere()) return;

		message.getAttribute(Tagging.ATTRIBUTE).ifPresent(tagging -> notifier.dispatch(message, tagging));
	}
}
