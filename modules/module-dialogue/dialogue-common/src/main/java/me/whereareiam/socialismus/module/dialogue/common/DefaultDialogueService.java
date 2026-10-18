package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.DialogueService;
import me.whereareiam.socialismus.module.dialogue.api.event.DialogueSendEvent;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.common.conversation.ConversationService;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageDeliveryCoordinator;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageFactory;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class DefaultDialogueService implements DialogueService {
	private final ConversationService conversationService;
	private final MessageFactory messageFactory;
	private final MessagingRequirements requirements;
	private final MessageDeliveryCoordinator deliveryCoordinator;
	private final DialogueNotices notices;
	private final PlayerRegistry playerRegistry;
	private final Provider<DialogueSettings> settings;
	private final EventManager eventManager;

	@Override
	public void send(@NotNull SocialismusPlayer sender, @NotNull String recipientName, @NotNull String content) {
		Optional<SocialismusPlayer> recipient = playerRegistry.getPlayerData(recipientName);
		String recipientAsSpelled = recipient.map(SocialismusPlayer::getUsername).orElse(recipientName);

		if (sender.getUsername().equalsIgnoreCase(recipientAsSpelled)) {
			notices.samePlayer(sender);
			return;
		}

		if (!requirements.allows(sender, recipient.orElse(null))) {
			notices.notAllowed(sender, recipientAsSpelled);
			return;
		}

		DialogueSendEvent event = new DialogueSendEvent(sender, recipientAsSpelled, recipient.orElse(null), content);
		eventManager.call(event);
		if (event.isCancelled()) return;

		Conversation conversation = conversationService.getOrCreateConversation(sender.getUsername(), recipientAsSpelled);
		Message message = messageFactory.createMessage(sender, recipientAsSpelled, event.getContent(), conversation.getId());

		deliveryCoordinator.processMessageDelivery(message);
		conversationService.addMessage(conversation, message);
	}

	@Override
	public void reply(@NotNull SocialismusPlayer sender, @NotNull String content) {
		List<Conversation> conversations = conversationService.getPlayerConversations(sender.getUsername());
		if (conversations.isEmpty()) {
			notices.noConversations(sender);
			return;
		}

		DialogueSettings.Messaging.Reply reply = settings.get().getMessaging().getReply();
		Conversation latest = conversations.getFirst();
		Instant answerableSince = Instant.now().minus(reply.getTimeout());
		if (latest.getLastActivity() == null || latest.getLastActivity().isBefore(answerableSince)) {
			notices.noPartner(sender, conversations);
			return;
		}

		String partner = latest.partnerOf(sender.getUsername());
		if (!reply.isToOffline() && !deliveryCoordinator.mayReach(partner)) {
			notices.partnerOffline(sender, partner);
			return;
		}

		send(sender, partner, content);
	}
}
