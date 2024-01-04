package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageFactory;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.module.bubblechat.BubbleChatService;
import me.whereareiam.socialismus.module.bubblechat.BubbleTriggerType;
import me.whereareiam.socialismus.util.MessageUtil;
import org.bukkit.entity.Player;

@Singleton
@CommandAlias("%command.main")
public class BubbleCommand extends CommandBase {
    private final MessageUtil messageUtil;
    private final CommandsConfig commands;
    private final MessagesConfig messages;
    private final BubbleChatService bubbleChatService;
    private final ChatMessageFactory chatMessageFactory;

    @Inject
    public BubbleCommand(MessageUtil messageUtil, CommandsConfig commands,
                         MessagesConfig messages, BubbleChatService bubbleChatService,
                         ChatMessageFactory chatMessageFactory) {
        this.messageUtil = messageUtil;
        this.commands = commands;
        this.messages = messages;
        this.bubbleChatService = bubbleChatService;
        this.chatMessageFactory = chatMessageFactory;
    }

    @Subcommand("%command.bubble")
    @CommandCompletion("@nothing")
    @CommandPermission("%permission.bubble")
    @Description("%description.bubble")
    @Syntax("%syntax.bubble")
    public void onCommand(CommandIssuer issuer, String message) {
        if (!issuer.isPlayer())
            return;

        Player player = issuer.getIssuer();

        ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, message, java.util.Optional.empty());
        bubbleChatService.distributeBubbleMessage(BubbleTriggerType.COMMAND, chatMessage);

        messageUtil.sendMessage(player, messages.commands.bubbleCommand.success);
    }

    @Override
    public boolean isEnabled() {
        return commands.announceCommand.enabled;
    }

    @Override
    public void addReplacements() {
        commandHelper.addReplacement(commands.bubbleCommand.subCommand, "command.bubble");
        commandHelper.addReplacement(commands.bubbleCommand.permission, "permission.bubble");
        commandHelper.addReplacement(messages.commands.bubbleCommand.description, "description.bubble");
        commandHelper.addReplacement(commands.bubbleCommand.syntax, "syntax.bubble");
    }
}
