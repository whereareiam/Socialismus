package me.whereareiam.socialismus.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.model.Chat;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.requirement.RequirementValidator;
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
    private final RequirementValidator requirementValidator;

    @Inject
    public ChatService(LoggerUtil loggerUtil, MessageUtil messageUtil, MessagesConfig messages,
                       ChatBroadcaster chatBroadcaster, RequirementValidator requirementValidator) {
        this.loggerUtil = loggerUtil;
        this.messageUtil = messageUtil;
        this.messages = messages;

        this.chatBroadcaster = chatBroadcaster;
        this.requirementValidator = requirementValidator;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void distributeMessage(ChatMessage chatMessage) {
        loggerUtil.debug("Distributing message: " + chatMessage.getContent());

        Player sender = chatMessage.getSender();
        Chat chat = chatMessage.getChat();

        if (!requirementValidator.validatePlayer(Module.CHAT, sender)) {
            messageUtil.sendMessage(sender, messages.chat.lackOfRequirements);
            loggerUtil.debug(sender.getName() + " didn't met requirements");
            return;
        }

        Collection<? extends Player> recipients = Bukkit.getServer().getOnlinePlayers();
        recipients = requirementValidator.validatePlayers(Module.CHAT, sender, recipients);

        if (recipients.size() == 1) {
            String noOnlinePlayers = messages.chat.noOnlinePlayers;
            if (noOnlinePlayers != null) {
                messageUtil.sendMessage(sender, noOnlinePlayers);
                return;
            }
        }

        boolean isPlayerNearby;
        if (chat.radius != -1) {
            isPlayerNearby = recipients.stream()
                    .anyMatch(player -> !sender.equals(player) && DistanceCalculatorUtil.calculateDistance(sender, player) <= chat.radius);

            if (!isPlayerNearby) {
                String noNearbyPlayers = messages.chat.noNearbyPlayers;
                if (noNearbyPlayers != null) {
                    messageUtil.sendMessage(sender, noNearbyPlayers);
                    return;
                }
            }
        }

        chatBroadcaster.broadcastMessage(chatMessage, recipients);
    }
}
