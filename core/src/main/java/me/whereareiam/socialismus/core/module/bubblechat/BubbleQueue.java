package me.whereareiam.socialismus.core.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.event.bubblechat.BeforeBubbleSendMessageEvent;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.core.Scheduler;
import org.bukkit.Bukkit;
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

	public void addMessage(BubbleMessage bubbleMessage) {
		BeforeBubbleSendMessageEvent event = new BeforeBubbleSendMessageEvent(bubbleMessage);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		bubbleMessage = event.getBubbleMessage();
		Player player = bubbleMessage.getSender();

		playerMessageQueues.computeIfAbsent(player, k -> new LinkedList<>()).add(bubbleMessage);
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
			scheduler.schedule(() -> processNextMessage(player), (long)message.getDisplayTime(), TimeUnit.SECONDS);
		} else {
			currentBubbleMessages.remove(player);
		}
	}
}

