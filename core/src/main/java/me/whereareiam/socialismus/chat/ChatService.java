package me.whereareiam.socialismus.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.model.Chat;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.requirement.RequirementValidator;
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

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        onlinePlayers = requirementValidator.validatePlayers(Module.CHAT, sender, onlinePlayers);

        if (chat.radius != -1 && onlinePlayers.size() == 1) {
            String noOnlinePlayers = messages.chat.noOnlinePlayers;
            if (noOnlinePlayers != null) {
                messageUtil.sendMessage(sender, noOnlinePlayers);
                return;
            }

            String noNearbyPlayers = messages.chat.noNearbyPlayers;
            if (noNearbyPlayers != null) {
                messageUtil.sendMessage(sender, noNearbyPlayers);
                return;
            }
        }

        chatBroadcaster.broadcastMessage(chatMessage, onlinePlayers);
    }
}
