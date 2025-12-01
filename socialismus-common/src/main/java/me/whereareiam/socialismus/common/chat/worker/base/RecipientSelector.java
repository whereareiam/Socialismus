package me.whereareiam.socialismus.common.chat.worker.base;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.api.Serializer;
import me.whereareiam.socialismus.input.WorkerProcessor;
import me.whereareiam.socialismus.input.event.chat.recipient.RecipientsSelectedEvent;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.DummyPlayer;
import me.whereareiam.socialismus.model.serializer.SerializerContent;
import me.whereareiam.socialismus.model.serializer.SerializerPlaceholder;
import me.whereareiam.socialismus.output.PlatformInteractor;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.util.EventUtil;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class RecipientSelector {
	private final RequirementEvaluator requirementEvaluator;
	private final Provider<ChatSettings> settingsProvider;
	private final Provider<ChatMessages> messagesProvider;
	private final PlatformInteractor interactor;

	@Inject
	public RecipientSelector(
			WorkerProcessor<ChatMessage> workerProcessor,
			RequirementEvaluator requirementEvaluator,
			Provider<ChatSettings> settings,
			Provider<ChatMessages> messages,
			PlatformInteractor interactor
	) {
		this.requirementEvaluator = requirementEvaluator;
		this.settingsProvider = settings;
		this.messagesProvider = messages;
		this.interactor = interactor;

		workerProcessor.addWorker(new Worker<>(this::selectRecipients, 100, true, false));
	}

	private ChatMessage selectRecipients(ChatMessage chatMessage) {
		if (chatMessage.getChat() == null) {
			chatMessage.setCancelled(true);
			return chatMessage;
		}

		final Chat chat = chatMessage.getChat();
		final DummyPlayer sender = chatMessage.getSender();
		final ChatSettings settings = settingsProvider.get();
		final ChatMessages messages = messagesProvider.get();

		final int beforeCount = chatMessage.getRecipients().size();

		Set<DummyPlayer> recipients = chatMessage.getRecipients();

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

		Set<DummyPlayer> finalRecipients = event.getNewRecipients();
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

	private boolean meetsRecipientRequirements(Chat chat, DummyPlayer recipient) {
		if (chat.getRequirements() == null) return true;
		var req = chat.getRequirements().get(Participants.RECIPIENT);
		if (req == null) return true;

		return requirementEvaluator.check(req, recipient);
	}

	private boolean isInSameRealm(DummyPlayer sender, DummyPlayer recipient) {
		if (PlatformType.isProxy())
			return Objects.equals(recipient.getServer(), sender.getServer());

		return Objects.equals(recipient.getLocation(), sender.getLocation());
	}

	private boolean isWithinRadius(DummyPlayer sender, DummyPlayer recipient, double radius) {
		return interactor.areWithinRange(sender.getUniqueId(), recipient.getUniqueId(), radius);
	}

	private void sendNoNearbyPlayersMessage(DummyPlayer sender, int radius, ChatMessages messages) {
		List<SerializerPlaceholder> placeholders = new ArrayList<>(1);
		placeholders.add(new SerializerPlaceholder("{radius}", String.valueOf(radius)));
		sender.sendMessage(Serializer.serialize(
				new SerializerContent(sender, placeholders, messages.getNoNearbyPlayers())
		));
	}
}