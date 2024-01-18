package me.whereareiam.socialismus.core.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.Scheduler;
import me.whereareiam.socialismus.core.module.bubblechat.message.BubbleMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Singleton
public class BubbleQueue {
	private final BubbleChatBroadcaster bubbleChatBroadcaster;
	private final Scheduler scheduler;

	private final Map<Player, Queue<BubbleMessage>> playerMessageQueues = new HashMap<>();
	private final Map<Player, BubbleMessage> currentBubbleMessages = new HashMap<>();

	@Inject
	public BubbleQueue(Scheduler scheduler, BubbleChatBroadcaster bubbleChatBroadcaster) {
		this.scheduler = scheduler;
		this.bubbleChatBroadcaster = bubbleChatBroadcaster;
	}

	public void addMessage(Player player, BubbleMessage message) {
		playerMessageQueues.computeIfAbsent(player, k -> new LinkedList<>()).add(message);
		if (! currentBubbleMessages.containsKey(player)) {
			processNextMessage(player);
		}
	}

	private void processNextMessage(Player player) {
		if (currentBubbleMessages.containsKey(player)) {
			bubbleChatBroadcaster.broadcastBubbleRemove(player);
		}

		Queue<BubbleMessage> queue = playerMessageQueues.get(player);
		if (queue != null && ! queue.isEmpty()) {
			BubbleMessage message = queue.poll();
			currentBubbleMessages.put(player, message);
			bubbleChatBroadcaster.broadcastBubble(message);
			scheduler.schedule(() -> processNextMessage(player), (long)message.displayTime(), TimeUnit.SECONDS);
		} else {
			currentBubbleMessages.remove(player);
		}
	}
}

