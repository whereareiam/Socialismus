package me.whereareiam.socialismus.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageBroadcaster;
import me.whereareiam.socialismus.chat.model.Chat;
import me.whereareiam.socialismus.chat.requirement.validator.RecipientRequirementValidator;
import me.whereareiam.socialismus.chat.requirement.validator.SenderRequirementValidator;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.util.DistanceCalculatorUtil;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

@Singleton
public class ChatService {
    private final LoggerUtil loggerUtil;
    private final FormatterUtil formatterUtil;
    private final MessagesConfig messages;

    private final ChatMessageBroadcaster chatMessageBroadcaster;

    private final RecipientRequirementValidator recipientRequirementValidator;
    private final SenderRequirementValidator senderRequirementValidator;

    @Inject
    public ChatService(LoggerUtil loggerUtil, FormatterUtil formatterUtil, MessagesConfig messages,
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
        loggerUtil.debug("Distributing message: " + chatMessage.getContent());

        Player sender = chatMessage.getSender();
        Audience senderAudience = (Audience) sender;

        Chat chat = chatMessage.getChat();

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

        boolean isPlayerNearby;
        if (chat.requirements.radius != -1) {
            isPlayerNearby = onlinePlayers.stream()
                    .anyMatch(player -> !sender.equals(player) && DistanceCalculatorUtil.calculateDistance(sender, player) <= chat.requirements.radius);

            if (!isPlayerNearby) {
                String noNearbyPlayers = messages.chat.noNearbyPlayers;
                if (noNearbyPlayers != null) {
                    senderAudience.sendMessage(formatterUtil.formatMessage(sender, noNearbyPlayers));
                    return;
                }
            }
        }

        onlinePlayers.stream()
                .filter(recipient -> recipientRequirementValidator.checkRequirements(recipient, chat))
                .filter(recipient -> chat.requirements.radius == -1 || DistanceCalculatorUtil.calculateDistance(sender, recipient) <= chat.requirements.radius)
                .forEach(recipient -> chatMessageBroadcaster.broadcastMessage(chatMessage, recipient));
    }
}
