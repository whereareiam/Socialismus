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
        if (messageQueue.size() == 1) {
            processNextMessage();
        }
    }

    private void processNextMessage() {
        if (!messageQueue.isEmpty()) {
            BubbleMessage bubbleMessage = messageQueue.peek();
            bubbleChatBroadcaster.broadcastBubble(bubbleMessage);
            scheduler.schedule(this::processNextMessage, (long) bubbleMessage.displayTime(), TimeUnit.SECONDS);
        } else {
            bubbleChatBroadcaster.broadcastBubbleRemove(player);
        }
    }

}
