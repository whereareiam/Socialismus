package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import com.google.inject.Inject;
import me.whereareiam.socialismus.chat.ChatService;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageFactory;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ChatCommandTemplate extends CommandBase {
    private final MessageUtil messageUtil;
    private final MessagesConfig messages;
    private final ChatMessageFactory chatMessageFactory;
    private final ChatService chatService;

    private Chat chat;

    @Inject
    public ChatCommandTemplate(LoggerUtil loggerUtil, MessageUtil messageUtil, MessagesConfig messages,
                               ChatMessageFactory chatMessageFactory, ChatService chatService) {
        this.messageUtil = messageUtil;
        this.messages = messages;
        this.chatMessageFactory = chatMessageFactory;
        this.chatService = chatService;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @CommandAlias("%command.chat")
    @CommandPermission("%permission.chat")
    @CommandCompletion("@nothing")
    public void onCommand(CommandIssuer issuer, String message) {
        if (!issuer.isPlayer())
            messageUtil.sendMessage(issuer, messages.commands.onlyForPlayer);

        Player player = issuer.getIssuer();

        ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, message, Optional.of(chat.usage.command));
        chatService.distributeMessage(chatMessage);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void addReplacements() {
        commandHelper.addReplacement(chat.usage.command, "command.chat");
        commandHelper.addReplacement(chat.requirements.sender.usePermission, "permission.chat");
    }
}
