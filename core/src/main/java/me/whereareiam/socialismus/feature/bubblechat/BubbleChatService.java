package me.whereareiam.socialismus.feature.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.feature.bubblechat.message.BubbleMessage;
import me.whereareiam.socialismus.feature.bubblechat.message.BubbleMessageProcessor;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.WorldPlayerUtil;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Singleton
public class BubbleChatService {
    private final Injector injector;
    private final LoggerUtil loggerUtil;
    private final WorldPlayerUtil worldPlayerUtil;

    private final BubbleMessageProcessor bubbleMessageProcessor;
    private final Map<Player, BubbleQueue> playerQueuesMap = new HashMap<>();

    @Inject
    public BubbleChatService(Injector injector, LoggerUtil loggerUtil, WorldPlayerUtil worldPlayerUtil,
                             BubbleMessageProcessor bubbleMessageProcessor) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.worldPlayerUtil = worldPlayerUtil;

        this.bubbleMessageProcessor = bubbleMessageProcessor;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void distributeBubbleMessage(ChatMessage chatMessage) {
        loggerUtil.debug("Distributing bubble message");

        Player player = chatMessage.getSender();
        Collection<Player> players = worldPlayerUtil.getPlayersInWorld(player.getWorld());
        Queue<BubbleMessage> queue = bubbleMessageProcessor.processMessage(chatMessage, players);
        loggerUtil.debug("Created a queue of " + queue.size() + " bubble messages");

        BubbleQueue bubbleQueue = playerQueuesMap.get(player);
        if (bubbleQueue == null) {
            bubbleQueue = injector.getInstance(BubbleQueue.class);
            bubbleQueue.setPlayer(player);
            playerQueuesMap.put(player, bubbleQueue);
        }

        while (!queue.isEmpty()) {
            bubbleQueue.addMessage(queue.poll());
        }
    }
}
