package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import me.whereareiam.socialismus.input.WorkerProcessor;
import me.whereareiam.socialismus.input.event.chat.recipient.RecipientsSelectedEvent;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.util.EventUtil;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class RecipientSelector {
	private final RequirementEvaluator requirementEvaluator;
	private final Provider<ChatSettings> settingsProvider;
	private final Provider<ChatMessages> messagesProvider;

	@Inject
	public RecipientSelector(
			WorkerProcessor<ChatMessage> workerProcessor,
			RequirementEvaluator requirementEvaluator,
			Provider<ChatSettings> settings,
			Provider<ChatMessages> messages
	) {
		this.requirementEvaluator = requirementEvaluator;
		this.settingsProvider = settings;
		this.messagesProvider = messages;

		workerProcessor.addWorker(new Worker<>(this::selectRecipients, 100, true, false));
	}

	private ChatMessage selectRecipients(ChatMessage chatMessage) {
		if (chatMessage.getChat() == null) {
			chatMessage.setCancelled(true);
			return chatMessage;
		}

		final Chat chat = chatMessage.getChat();
		final SocialismusPlayer sender = chatMessage.getSender();
		final ChatSettings settings = settingsProvider.get();
		final ChatMessages messages = messagesProvider.get();

		final int beforeCount = chatMessage.getRecipients().size();

		Collection<SocialismusPlayer> recipients = chatMessage.getRecipients();

		final int radius = determineRadius(chatMessage, chat);

		if (radius > 0) {
			recipients = recipients.stream()
					.filter(r -> isInSameRealm(sender, r))
					.filter(r -> isWithinRadius(sender, r, radius))
					.filter(r -> meetsRecipientRequirements(chat, r))
					.collect(Collectors.toSet());

			// If nobody nearby (<=1 usually means only the sender), optionally notify & cancel
			if (recipients.size() <= 1 && settings.isNotifyNoNearbyPlayers()) {
				sendNoNearbyPlayersMessage(sender, radius, messages);
				chatMessage.setCancelled(true);
				return chatMessage;
			}
		} else {
			recipients = recipients.stream()
					.filter(r -> meetsRecipientRequirements(chat, r))
					.collect(Collectors.toSet());
		}

		RecipientsSelectedEvent event = new RecipientsSelectedEvent(chatMessage, recipients, chatMessage.isCancelled());
		EventUtil.callEvent(event, () -> chatMessage.setRecipients(event.getNewRecipients()));

		Collection<SocialismusPlayer> finalRecipients = event.getNewRecipients();
		Logger.debug("Recipients before: " + beforeCount + ", after: " + finalRecipients.size());

		if (finalRecipients.isEmpty()) {
			chatMessage.setCancelled(true);
			return chatMessage;
		}

		if (finalRecipients.size() == 1 && settings.isNotifyNoPlayers()) {
			sender.sendMessage(Serializer.serialize(sender, messages.getNoPlayers()));
			chatMessage.setCancelled(true);
		}

		return chatMessage;
	}

	private int determineRadius(ChatMessage message, Chat chat) {
		if (message.getTrigger() != null && message.getTrigger().getRadius() != null)
			return message.getTrigger().getRadius();

		if (chat.getTriggers() != null) {
			return chat.getTriggers().stream()
					.map(t -> t.getRadius() == null ? 0 : t.getRadius())
					.filter(r -> r > 0)
					.findFirst()
					.orElse(0);
		}

		return 0;
	}

	private boolean meetsRecipientRequirements(Chat chat, SocialismusPlayer recipient) {
		if (chat.getRequirements() == null) return true;
		var req = chat.getRequirements().get(Participants.RECIPIENT);
		if (req == null) return true;

		return requirementEvaluator.check(req, recipient);
	}

	private boolean isInSameRealm(SocialismusPlayer sender, SocialismusPlayer recipient) {
		if (PlatformType.isProxy())
			return Objects.equals(recipient.getServer(), sender.getServer());

		return Objects.equals(recipient.getLocation(), sender.getLocation());
	}

	private boolean isWithinRadius(SocialismusPlayer sender, SocialismusPlayer recipient, double radius) {
		return sender.isWithinRange(recipient, radius);
	}

	private void sendNoNearbyPlayersMessage(SocialismusPlayer sender, int radius, ChatMessages messages) {
		sender.sendMessage(Serializer.serialize(
				SerializerContent.builder()
						.receiver(sender)
						.message(messages.getNoNearbyPlayers())
						.placeholder("{radius}", String.valueOf(radius))
						.build()
		));
	}
}