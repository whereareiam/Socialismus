package me.whereareiam.socialismus.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.model.Chat;
import me.whereareiam.socialismus.chat.requirement.validator.RecipientRequirementValidator;
import me.whereareiam.socialismus.chat.requirement.validator.SenderRequirementValidator;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.feature.statistics.ChatMessageStatistic;
import me.whereareiam.socialismus.util.DistanceCalculatorUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

@Singleton
public class ChatService {
    private final LoggerUtil loggerUtil;
    private final MessageUtil messageUtil;
    private final MessagesConfig messages;

    private final ChatBroadcaster chatBroadcaster;

    private final RecipientRequirementValidator recipientRequirementValidator;
    private final SenderRequirementValidator senderRequirementValidator;

    private final ChatMessageStatistic chatMessageStatistic;

    @Inject
    public ChatService(LoggerUtil loggerUtil, MessageUtil messageUtil, MessagesConfig messages,
                       ChatBroadcaster chatBroadcaster,
                       RecipientRequirementValidator recipientRequirementValidator,
                       SenderRequirementValidator senderRequirementValidator,
                       ChatMessageStatistic chatMessageStatistic) {
        this.loggerUtil = loggerUtil;
        this.messageUtil = messageUtil;
        this.messages = messages;

        this.chatBroadcaster = chatBroadcaster;

        this.recipientRequirementValidator = recipientRequirementValidator;
        this.senderRequirementValidator = senderRequirementValidator;
        this.chatMessageStatistic = chatMessageStatistic;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void distributeMessage(ChatMessage chatMessage) {
        loggerUtil.debug("Distributing message: " + chatMessage.getContent());

        Player sender = chatMessage.getSender();
        Chat chat = chatMessage.getChat();

        if (!senderRequirementValidator.checkRequirements(sender, chat)) {
            messageUtil.sendMessage(sender, messages.chat.lackOfRequirements);
            loggerUtil.debug(sender.getName() + " didn't met requirements");
            return;
        }

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();

        boolean isPlayerNearby;
        if (chat.requirements.radius != -1) {
            isPlayerNearby = onlinePlayers.stream()
                    .anyMatch(player -> !sender.equals(player) && DistanceCalculatorUtil.calculateDistance(sender, player) <= chat.requirements.radius);

            if (!isPlayerNearby) {
                String noNearbyPlayers = messages.chat.noNearbyPlayers;
                if (noNearbyPlayers != null) {
                    messageUtil.sendMessage(sender, noNearbyPlayers);
                    return;
                }
            }
        }

        if (onlinePlayers.size() == 1) {
            String noOnlinePlayers = messages.chat.noOnlinePlayers;
            if (noOnlinePlayers != null) {
                messageUtil.sendMessage(sender, noOnlinePlayers);
                return;
            }
        }

        chatMessageStatistic.incrementStatistic(chat.id);
        onlinePlayers.stream()
                .filter(recipient -> recipientRequirementValidator.checkRequirements(recipient, chat))
                .filter(recipient -> chat.requirements.radius == -1 || DistanceCalculatorUtil.calculateDistance(sender, recipient) <= chat.requirements.radius)
                .forEach(recipient -> chatBroadcaster.broadcastMessage(chatMessage, recipient));
    }
}
