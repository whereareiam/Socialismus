package me.whereareiam.socialismus.module.bubbler.common.worker.recipient;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.type.chat.Participants;

import java.util.Collection;
import java.util.stream.Collectors;

@Singleton
public class RecipientSelector {
	private final RequirementEvaluatorService requirementEvaluator;
	private final Provider<BubblerSettings> settings;
	private final Provider<BubblerMessages> messages;

	@Inject
	public RecipientSelector(
			WorkerProcessor<BubbleMessage> workerProcessor,
			RequirementEvaluatorService requirementEvaluator,
			Provider<BubblerSettings> settings,
			Provider<BubblerMessages> messages
	) {
		this.requirementEvaluator = requirementEvaluator;
		this.settings = settings;
		this.messages = messages;

		workerProcessor.addWorker(new Worker<>(this::selectRecipients, 100, true, false));
	}

	private BubbleMessage selectRecipients(BubbleMessage bubbleMessage) {
		if (bubbleMessage.getBubble() == null) {
			bubbleMessage.setCancelled(true);
			return bubbleMessage;
		}

		int minimumRecipients = settings.get().getMinRecipients();

		Bubble bubble = bubbleMessage.getBubble();
		SocialismusPlayer sender = bubbleMessage.getSender();

		int oldRecipients = bubbleMessage.getRecipients().size();
		Collection<SocialismusPlayer> recipients = bubbleMessage.getRecipients();

		recipients = recipients.parallelStream()
				.filter(recipient -> isWithinRadius(sender, recipient, bubble.getDisplay().getRadius()))
				.filter(recipient -> requirementEvaluator.check(bubble.getRequirements().get(Participants.RECIPIENT), recipient))
				.collect(Collectors.toSet());

		if (recipients.size() < minimumRecipients) bubbleMessage.setCancelled(true);
		if (recipients.size() < minimumRecipients && settings.get().getNotify().isNotifyNoNearbyPlayers()) {
			sender.sendMessage(Serializer.serialize(sender, messages.get().getNoNearbyPlayers()));
			return bubbleMessage;
		}

		bubbleMessage.setRecipients(recipients);
		Logger.debug("Recipients before: " + oldRecipients + ", after: " + bubbleMessage.getRecipients().size());

		if (recipients.size() < minimumRecipients && settings.get().getNotify().isNotifyNoPlayers())
			sender.sendMessage(Serializer.serialize(sender, messages.get().getNoPlayers()));

		return bubbleMessage;
	}

	private boolean isWithinRadius(SocialismusPlayer sender, SocialismusPlayer recipient, double radius) {
		return sender.isWithinRange(recipient, radius);
	}
}