package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.input.WorkerProcessor;
import me.whereareiam.socialismus.input.event.chat.recipient.RecipientsResolvedEvent;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.util.EventUtil;

@Singleton
public class RecipientResolver {
	private final PlayerRegistry playerRegistry;

	@Inject
	public RecipientResolver(WorkerProcessor<ChatMessage> workerProcessor, PlayerRegistry playerRegistry) {
		this.playerRegistry = playerRegistry;
		workerProcessor.addWorker(new Worker<>(this::resolveRecipients, 75, true, false));
	}

	private ChatMessage resolveRecipients(ChatMessage chatMessage) {
		if (!chatMessage.getRecipients().isEmpty()) return chatMessage;

		Logger.debug("Resolving recipients for chat message from %s", chatMessage.getSender().getUsername());
		EventUtil.callEvent(new RecipientsResolvedEvent(chatMessage, chatMessage.isCancelled()),
				() -> chatMessage.setRecipients(playerRegistry.getPlayers())
		);

		return chatMessage;
	}
}
