package me.whereareiam.socialismus.common.chat.broadcast;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.chat.render.ChatRenderService;
import me.whereareiam.socialismus.type.BroadcastTarget;
import net.kyori.adventure.text.Component;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatBroadcaster {
	private final PlatformInteractor platformInteractor;
	private final ChatRenderService chatRenderService;

	public void broadcast(FormattedChatMessage chatMessage) {
		Component consoleMessage = chatRenderService.render(chatMessage, chatMessage.getSender());
		platformInteractor.broadcast(consoleMessage, BroadcastTarget.CONSOLE);

		chatMessage.getRecipients().forEach(recipient ->
				recipient.sendMessage(chatRenderService.render(chatMessage, recipient))
		);
	}
}
