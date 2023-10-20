package me.whereareiam.socialismus.feature.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Scheduler;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.feature.bubblechat.message.BubbleMessage;
import me.whereareiam.socialismus.feature.bubblechat.message.BubbleMessageProcessor;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.WorldPlayerUtil;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Singleton
public class BubbleChatService {
    private final LoggerUtil loggerUtil;
    private final WorldPlayerUtil worldPlayerUtil;
    private final Scheduler scheduler;

    private final BubbleMessageProcessor bubbleMessageProcessor;
    private final BubbleChatBroadcaster bubbleChatBroadcaster;

    @Inject
    public BubbleChatService(LoggerUtil loggerUtil, WorldPlayerUtil worldPlayerUtil,
                             Scheduler scheduler, BubbleMessageProcessor bubbleMessageProcessor,
                             BubbleChatBroadcaster bubbleChatBroadcaster) {
        this.loggerUtil = loggerUtil;
        this.worldPlayerUtil = worldPlayerUtil;
        this.scheduler = scheduler;

        this.bubbleMessageProcessor = bubbleMessageProcessor;
        this.bubbleChatBroadcaster = bubbleChatBroadcaster;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void distributeBubbleMessage(ChatMessage chatMessage) {
        loggerUtil.debug("Distributing bubble message");
        Player player = chatMessage.getSender();
        Collection<Player> players = worldPlayerUtil.getPlayersInWorld(player.getWorld());

        Queue<BubbleMessage> queue = bubbleMessageProcessor.processMessage(chatMessage, players);

        double delay = 0;
        BubbleMessage lastBubbleMessage = null;
        while (!queue.isEmpty()) {
            BubbleMessage bubbleMessage = queue.poll();
            if (lastBubbleMessage == null) {
                bubbleChatBroadcaster.broadcastBubble(bubbleMessage);
            } else {
                scheduler.schedule(() -> bubbleChatBroadcaster.broadcastBubble(bubbleMessage), (long) delay, TimeUnit.SECONDS);
            }
            lastBubbleMessage = bubbleMessage;
            delay += lastBubbleMessage.displayTime();
        }

        if (lastBubbleMessage != null) {
            scheduler.schedule(() -> bubbleChatBroadcaster.broadcastBubbleRemove(player), (long) delay, TimeUnit.SECONDS);
        }
    }
}
