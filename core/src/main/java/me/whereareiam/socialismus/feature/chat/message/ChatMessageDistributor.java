package me.whereareiam.socialismus.feature.chat.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.feature.chat.Chat;
import me.whereareiam.socialismus.feature.chat.requirement.validator.RecipientRequirementValidator;
import me.whereareiam.socialismus.feature.chat.requirement.validator.SenderRequirementValidator;
import me.whereareiam.socialismus.util.DistanceCalculatorUtil;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

@Singleton
public class ChatMessageDistributor {
    private final LoggerUtil loggerUtil;
    private final FormatterUtil formatterUtil;
    private final MessagesConfig messages;

    private final ChatMessageBroadcaster chatMessageBroadcaster;

    private final RecipientRequirementValidator recipientRequirementValidator;
    private final SenderRequirementValidator senderRequirementValidator;

    @Inject
    public ChatMessageDistributor(LoggerUtil loggerUtil, FormatterUtil formatterUtil, MessagesConfig messages,
                                  ChatMessageBroadcaster chatMessageBroadcaster,
                                  RecipientRequirementValidator recipientRequirementValidator,
                                  SenderRequirementValidator senderRequirementValidator
    ) {
        this.loggerUtil = loggerUtil;
        this.formatterUtil = formatterUtil;
        this.messages = messages;

        this.chatMessageBroadcaster = chatMessageBroadcaster;

        this.recipientRequirementValidator = recipientRequirementValidator;
        this.senderRequirementValidator = senderRequirementValidator;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void distributeMessage(ChatMessage chatMessage) {
        loggerUtil.debug("Distributing message: " + chatMessage.content());

        Player sender = chatMessage.sender();
        Audience senderAudience = (Audience) sender;

        Chat chat = chatMessage.chat();

        if (!senderRequirementValidator.checkRequirements(sender, chat)) {
            senderAudience.sendMessage(formatterUtil.formatMessage(sender, messages.chat.lackOfRequirements));
            loggerUtil.debug(sender.getName() + " didn't met requirements");
            return;
        }

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();

        if (onlinePlayers.isEmpty() || (onlinePlayers.size() == 1 && onlinePlayers.contains(sender))) {
            String noOnlinePlayers = messages.chat.noOnlinePlayers;
            if (noOnlinePlayers != null) {
                senderAudience.sendMessage(formatterUtil.formatMessage(sender, noOnlinePlayers));
                return;
            }
        }

        boolean isPlayerNearby = false;
        if (chat.radius != -1) {
            for (Player player : onlinePlayers) {
                if (!sender.equals(player) && DistanceCalculatorUtil.calculateDistance(sender, player) <= chat.radius) {
                    isPlayerNearby = true;
                    break;
                }
            }

            if (!isPlayerNearby) {
                String noNearbyPlayers = messages.chat.noNearbyPlayers;
                if (noNearbyPlayers != null) {
                    senderAudience.sendMessage(formatterUtil.formatMessage(sender, noNearbyPlayers));
                    return;
                }
            }
        }

        for (Player recipient : onlinePlayers) {
            if (recipientRequirementValidator.checkRequirements(recipient, chat)) {
                chatMessageBroadcaster.broadcastMessage(chatMessage, recipient);
            }
        }
    }
}
