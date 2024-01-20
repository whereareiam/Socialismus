package me.whereareiam.socialismus.core.module.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.type.BubbleTriggerType;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.WorldPlayerUtil;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Singleton
public class BubbleChatService {
	private final Injector injector;
	private final LoggerUtil loggerUtil;
	private final BubbleChatConfig bubbleChatConfig;

	private final BubbleChatRequirementValidator bubbleChatRequirementValidator;
	private final BubbleMessageProcessor bubbleMessageProcessor;
	private final Map<Player, BubbleQueue> playerQueuesMap = new HashMap<>();

	@Inject
	public BubbleChatService(Injector injector, LoggerUtil loggerUtil, BubbleChatConfig bubbleChatConfig,

	                         BubbleChatRequirementValidator bubbleChatRequirementValidator,
	                         BubbleMessageProcessor bubbleMessageProcessor) {
		this.injector = injector;
		this.loggerUtil = loggerUtil;
		this.bubbleChatConfig = bubbleChatConfig;

		this.bubbleChatRequirementValidator = bubbleChatRequirementValidator;
		this.bubbleMessageProcessor = bubbleMessageProcessor;

		loggerUtil.trace("Initializing class: " + this);
	}

	public void distributeBubbleMessage(BubbleTriggerType triggerType, ChatMessage chatMessage) {
		BubbleTriggerType configTriggerType = bubbleChatConfig.settings.triggerType;

		if (!(configTriggerType == triggerType
				|| (configTriggerType == BubbleTriggerType.CHAT_COMMAND &&
				(triggerType == BubbleTriggerType.CHAT || triggerType == BubbleTriggerType.COMMAND)))
		) return;

		loggerUtil.debug("Distributing bubble message");

		Player sender = chatMessage.getSender();
		if (!bubbleChatRequirementValidator.validatePlayer(chatMessage))
			return;

		Collection<? extends Player> onlinePlayers = WorldPlayerUtil.getPlayersInWorld(sender.getWorld());
		onlinePlayers = bubbleChatRequirementValidator.validatePlayers(sender, onlinePlayers);

		Queue<BubbleMessage> queue = bubbleMessageProcessor.processMessage(chatMessage, onlinePlayers);
		loggerUtil.debug("Created a queue of " + queue.size() + " bubble messages");

		BubbleQueue bubbleQueue = playerQueuesMap.get(sender);
		if (bubbleQueue == null) {
			bubbleQueue = injector.getInstance(BubbleQueue.class);
			playerQueuesMap.put(sender, bubbleQueue);
		}

		while (!queue.isEmpty()) {
			bubbleQueue.addMessage(queue.poll());
		}
	}
}
