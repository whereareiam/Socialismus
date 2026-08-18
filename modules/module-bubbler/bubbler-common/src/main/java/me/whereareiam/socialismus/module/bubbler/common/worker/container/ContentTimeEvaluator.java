package me.whereareiam.socialismus.module.bubbler.common.worker.container;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.util.ComponentUtil;

@Singleton
public class ContentTimeEvaluator {

	@Inject
	public ContentTimeEvaluator(WorkerProcessor<BubbleMessage> workerProcessor) {

		workerProcessor.addWorker(new Worker<>(this::evaluateTime, 200, true, false));
	}

	public BubbleMessage evaluateTime(BubbleMessage bubbleMessage) {
		Logger.debug("Evaluating display time for " + bubbleMessage.getSender().getUsername());

		bubbleMessage.getGroups()
				.forEach(group -> group.getLines()
						.forEach(line -> {
							long displayTime =
									calculateDisplayTime(
											ComponentUtil.toPlain(line.getContent()), bubbleMessage.getBubble());
							line.setDisplayTime(displayTime);
						}));

		Logger.debug(
				"Display time for "
						+ bubbleMessage.getSender().getUsername()
						+ " is "
						+ bubbleMessage.getGroups().stream().mapToLong(group ->
						group.getLines().stream().mapToLong(BubbleLine::getDisplayTime).sum()
				).sum());

		return bubbleMessage;
	}

	private long calculateDisplayTime(String content, Bubble bubble) {
		int symbolCount = content.length();
		double timePerSymbol = bubble.getDisplay().getTimePerSymbol();
		double minimumTime = bubble.getDisplay().getMinimumTime();

		long calculatedTime = (long) (symbolCount * timePerSymbol);
		return Math.max(calculatedTime, (long) minimumTime);
	}
}
