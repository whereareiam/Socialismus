package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.input.WorkerProcessor;
import me.whereareiam.socialismus.input.container.PlayerContainerService;
import me.whereareiam.socialismus.input.event.chat.recipient.RecipientsResolvedEvent;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.util.EventUtil;

@Singleton
public class RecipientResolver {
	private final PlayerContainerService playerContainer;

	@Inject
	public RecipientResolver(WorkerProcessor<ChatMessage> workerProcessor, PlayerContainerService playerContainer) {
		this.playerContainer = playerContainer;
		workerProcessor.addWorker(new Worker<>(this::resolveRecipients, 75, true, false));
	}

	private ChatMessage resolveRecipients(ChatMessage chatMessage) {
		if (!chatMessage.getRecipients().isEmpty()) return chatMessage;

		Logger.debug("Resolving recipients for chat message from %s", chatMessage.getSender().getUsername());
		EventUtil.callEvent(new RecipientsResolvedEvent(chatMessage, chatMessage.isCancelled()),
				() -> chatMessage.setRecipients(playerContainer.getPlayers())
		);

		return chatMessage;
	}
}
