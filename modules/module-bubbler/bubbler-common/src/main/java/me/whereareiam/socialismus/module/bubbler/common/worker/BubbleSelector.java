package me.whereareiam.socialismus.module.bubbler.common.worker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerMessages;
import me.whereareiam.socialismus.module.bubbler.api.model.config.BubblerSettings;
import me.whereareiam.socialismus.module.bubbler.common.requirement.BubbleRequirements;
import me.whereareiam.socialismus.registry.WorkerProcessor;

import java.util.List;

@Singleton
public class BubbleSelector {
	private final BubbleRequirements requirements;

	// Configs
	private final Provider<BubblerMessages> bubblerMessages;
	private final Provider<BubblerSettings> bubblerSettings;
	private final Provider<List<Bubble>> bubbles;

	@Inject
	public BubbleSelector(
			BubbleRequirements requirements,
			WorkerProcessor<BubbleMessage> workerProcessor,
			Provider<BubblerMessages> bubblerMessages,
			Provider<BubblerSettings> bubblerSettings,
			Provider<List<Bubble>> bubbles
	) {
		this.requirements = requirements;
		this.bubblerMessages = bubblerMessages;
		this.bubblerSettings = bubblerSettings;
		this.bubbles = bubbles;

		workerProcessor.addWorker(new Worker<>(this::selectBubble, 50, true, false));
	}

	public BubbleMessage selectBubble(BubbleMessage bubbleMessage) {
		Logger.debug("Selecting bubble for user " + bubbleMessage.getSender().getUsername());

		Bubble bubble = bubbles.get().stream()
				.filter(b -> requirements.selects(b, bubbleMessage))
				.findFirst()
				.orElse(null);

		if (bubble == null) {
			notifyAboutAbsentBubble(bubbleMessage);
			bubbleMessage.setCancelled(true);

			return bubbleMessage;
		}

		Logger.debug("Selected bubble " + bubble.getId() + " for user " + bubbleMessage.getSender().getUsername());
		bubbleMessage.setBubble(bubble);

		return bubbleMessage;
	}

	private void notifyAboutAbsentBubble(BubbleMessage bubbleMessage) {
		if (!bubblerSettings.get().getNotifications().isNoBubbleSelected()) return;

		bubbleMessage.getSender().sendMessage(
				Serializer.serialize(bubbleMessage.getSender(), bubblerMessages.get().getNoBubbleSelected())
		);
	}
}
