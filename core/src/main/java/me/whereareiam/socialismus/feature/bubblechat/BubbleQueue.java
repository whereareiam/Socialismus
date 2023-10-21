package me.whereareiam.socialismus.feature.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Scheduler;
import me.whereareiam.socialismus.feature.bubblechat.message.BubbleMessage;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Singleton
public class BubbleQueue {
    private final Queue<BubbleMessage> messageQueue;
    private final BubbleChatBroadcaster bubbleChatBroadcaster;
    private final Scheduler scheduler;
    private Player player;
    private BubbleMessage currentBubbleMessage;

    @Inject
    public BubbleQueue(Scheduler scheduler, BubbleChatBroadcaster bubbleChatBroadcaster) {
        this.scheduler = scheduler;
        this.bubbleChatBroadcaster = bubbleChatBroadcaster;
        this.messageQueue = new LinkedList<>();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addMessage(BubbleMessage message) {
        messageQueue.add(message);
        if (currentBubbleMessage == null) {
            processNextMessage();
        }
    }

    private void processNextMessage() {
        if (currentBubbleMessage != null) {
            bubbleChatBroadcaster.broadcastBubbleRemove(player);
        }

        if (!messageQueue.isEmpty()) {
            currentBubbleMessage = messageQueue.poll();
            bubbleChatBroadcaster.broadcastBubble(currentBubbleMessage);
            scheduler.schedule(this::processNextMessage, (long) currentBubbleMessage.displayTime(), TimeUnit.SECONDS);
        } else {
            currentBubbleMessage = null;
        }
    }
}
