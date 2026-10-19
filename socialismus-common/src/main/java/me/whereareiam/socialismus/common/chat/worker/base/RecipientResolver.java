package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.event.chat.recipient.RecipientsResolvedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.chat.ChatOwnershipService;
import me.whereareiam.socialismus.util.EventUtil;

@Singleton
public class RecipientResolver {
	private final PlayerRegistry playerRegistry;
	private final ChatOwnershipService ownership;

	@Inject
	public RecipientResolver(
			WorkerProcessor<ChatMessage> workerProcessor,
			PlayerRegistry playerRegistry,
			ChatOwnershipService ownership
	) {
		this.playerRegistry = playerRegistry;
		this.ownership = ownership;
		workerProcessor.addWorker(new Worker<>(this::resolveRecipients, 75, true, false));
	}

	private ChatMessage resolveRecipients(ChatMessage chatMessage) {
		if (!chatMessage.getRecipients().isEmpty()) {
			// Where several Socialismus share players, each delivers only to the players whose chat it handles.
			chatMessage.setRecipients(chatMessage.getRecipients().stream().filter(ownership::handles).toList());
			return chatMessage;
		}

		Logger.debug("Resolving recipients for chat message from %s", chatMessage.getSender().getUsername());
		EventUtil.callEvent(new RecipientsResolvedEvent(chatMessage, chatMessage.isCancelled()),
				() -> chatMessage.setRecipients(playerRegistry.getPlayers().stream().filter(ownership::handles).toList())
		);

		return chatMessage;
	}
}
