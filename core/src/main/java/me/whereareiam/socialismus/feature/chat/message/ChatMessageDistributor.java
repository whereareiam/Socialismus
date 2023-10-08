package me.whereareiam.socialismus.feature.chat.message;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.feature.chat.Chat;
import me.whereareiam.socialismus.feature.chat.requirement.validator.RecipientRequirementValidator;
import me.whereareiam.socialismus.feature.chat.requirement.validator.SenderRequirementValidator;
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

    public void broadcastMessage(ChatMessage chatMessage) {
        loggerUtil.debug("Broadcasting message: " + chatMessage.content());

        Player sender = chatMessage.sender();
        Audience senderAudience = (Audience) sender;

        Chat chat = chatMessage.chat();

        if (!senderRequirementValidator.checkRequirements(sender, chat)) {
            senderAudience.sendMessage(formatterUtil.formatMessage(sender, messages.chat.lackOfRequirements));
            loggerUtil.debug(sender.getName() + " didn't met requirements");
            return;
        }

        Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();

        for (Player recipient : onlinePlayers) {
            if (recipientRequirementValidator.checkRequirements(recipient, chat)) {
                chatMessageBroadcaster.broadcastMessage(chatMessage, recipient);
            }
        }
    }
}
