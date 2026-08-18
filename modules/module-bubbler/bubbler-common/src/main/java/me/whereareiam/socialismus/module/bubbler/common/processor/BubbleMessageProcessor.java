package me.whereareiam.socialismus.module.bubbler.common.processor;

import com.google.inject.Singleton;
import lombok.Getter;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.registry.WorkerProcessor;

import java.util.Comparator;
import java.util.LinkedList;

@Getter
@Singleton
public class BubbleMessageProcessor implements WorkerProcessor<BubbleMessage> {
	private final LinkedList<Worker<BubbleMessage>> workers = new LinkedList<>();

	public BubbleMessage process(BubbleMessage bubbleMessage) {
		for (Worker<BubbleMessage> worker : workers) {
			bubbleMessage = worker.getFunction().apply(bubbleMessage);

			if (bubbleMessage.isCancelled()) break;
		}

		return bubbleMessage;
	}

	@Override
	public void addWorker(Worker<BubbleMessage> worker) {
		if (workers.stream().noneMatch(w -> w.getPriority() == worker.getPriority())) {
			workers.add(worker);
			workers.sort(Comparator.comparingInt(Worker::getPriority));
		}
	}

	@Override
	public boolean removeWorker(Worker<BubbleMessage> worker) {
		if (!worker.isRemovable()) return false;
		return workers.remove(worker);
	}
}
