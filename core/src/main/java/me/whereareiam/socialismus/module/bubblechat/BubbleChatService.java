package me.whereareiam.socialismus.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.module.bubblechat.message.BubbleMessage;
import me.whereareiam.socialismus.module.bubblechat.message.BubbleMessageProcessor;
import me.whereareiam.socialismus.requirement.RequirementValidator;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
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
    private final MessageUtil messageUtil;
    private final MessagesConfig messagesConfig;

    private final RequirementValidator requirementValidator;

    private final BubbleMessageProcessor bubbleMessageProcessor;
    private final Map<Player, BubbleQueue> playerQueuesMap = new HashMap<>();

    @Inject
    public BubbleChatService(Injector injector, LoggerUtil loggerUtil,
                             MessageUtil messageUtil, MessagesConfig messagesConfig,
                             RequirementValidator requirementValidator,
                             BubbleMessageProcessor bubbleMessageProcessor) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.messageUtil = messageUtil;
        this.messagesConfig = messagesConfig;
        this.requirementValidator = requirementValidator;

        this.bubbleMessageProcessor = bubbleMessageProcessor;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void distributeBubbleMessage(ChatMessage chatMessage) {
        loggerUtil.debug("Distributing bubble message");

        Player sender = chatMessage.getSender();
        if (!requirementValidator.validatePlayer(Module.BUBBLECHAT, sender)) {
            String message = messagesConfig.bubblechat.noSendPermission;
            if (message != null) {
                messageUtil.sendMessage(sender, message);
            }
            return;
        }

        Collection<Player> recipients = WorldPlayerUtil.getPlayersInWorld(sender.getWorld());
        recipients = requirementValidator.validatePlayers(Module.BUBBLECHAT, sender, recipients);

        Queue<BubbleMessage> queue = bubbleMessageProcessor.processMessage(chatMessage, recipients);
        loggerUtil.debug("Created a queue of " + queue.size() + " bubble messages");

        BubbleQueue bubbleQueue = playerQueuesMap.get(sender);
        if (bubbleQueue == null) {
            bubbleQueue = injector.getInstance(BubbleQueue.class);
            playerQueuesMap.put(sender, bubbleQueue);
        }

        while (!queue.isEmpty()) {
            bubbleQueue.addMessage(sender, queue.poll());
        }
    }
}
