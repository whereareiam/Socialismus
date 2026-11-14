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
import me.whereareiam.socialismus.module.bubbler.common.condition.BubbleConditions;
import me.whereareiam.socialismus.registry.WorkerProcessor;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Singleton
public class RecipientSelector {
	private final BubbleConditions conditions;
	private final Provider<BubblerSettings> settings;
	private final Provider<BubblerMessages> messages;

	@Inject
	public RecipientSelector(
			WorkerProcessor<BubbleMessage> workerProcessor,
			BubbleConditions conditions,
			Provider<BubblerSettings> settings,
			Provider<BubblerMessages> messages
	) {
		this.conditions = conditions;
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
		Collection<SocialismusPlayer> recipients = conditions.of(bubble)
				.map(places -> bubbleMessage.getRecipients().parallelStream()
						.filter(recipient -> places.reaches(recipient, bubbleMessage))
						.collect(Collectors.toSet()))
				.orElseGet(HashSet::new);

		if (recipients.size() < minimumRecipients) bubbleMessage.setCancelled(true);
		if (recipients.size() < minimumRecipients && settings.get().getNotifications().isNoNearbyPlayers()) {
			sender.sendMessage(Serializer.serialize(sender, messages.get().getNoNearbyPlayers()));
			return bubbleMessage;
		}

		bubbleMessage.setRecipients(recipients);
		Logger.debug("Recipients before: " + oldRecipients + ", after: " + bubbleMessage.getRecipients().size());

		if (recipients.size() < minimumRecipients && settings.get().getNotifications().isNoPlayers())
			sender.sendMessage(Serializer.serialize(sender, messages.get().getNoPlayers()));

		return bubbleMessage;
	}
}