package me.whereareiam.socialismus.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageProcessor;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class ChatBroadcaster {
    private final LoggerUtil loggerUtil;
    private final FormatterUtil formatterUtil;
    private final MessageUtil messageUtil;

    private final Set<ChatMessageProcessor> chatMessageProcessors;

    @Inject
    public ChatBroadcaster(LoggerUtil loggerUtil, FormatterUtil formatterUtil,
                           MessageUtil messageUtil, Set<ChatMessageProcessor> chatMessageProcessors) {
        this.loggerUtil = loggerUtil;
        this.formatterUtil = formatterUtil;
        this.messageUtil = messageUtil;

        this.chatMessageProcessors = new HashSet<>(chatMessageProcessors);
    }

    public void broadcastMessage(ChatMessage chatMessage, Collection<? extends Player> recipients) {
        Component finalMessage = createFinalMessage(chatMessage);

        recipients.forEach(recipient -> messageUtil.sendMessage(recipient, finalMessage));
        loggerUtil.info("[" + chatMessage.getChat().id.toUpperCase() + "] " + chatMessage.getSender().getName() + ": " + PlainTextComponentSerializer.plainText().serialize(chatMessage.getContent()));
    }

    private Component createFinalMessage(ChatMessage chatMessage) {
        for (ChatMessageProcessor processor : chatMessageProcessors) {
            chatMessage = processor.process(chatMessage);
        }

        Chat chat = chatMessage.getChat();
        Component messageFormat = formatterUtil.formatMessage(chatMessage.getSender(), chat.messageFormat);
        Component hoverFormat = createHoverFormat(chat.hoverFormat, chatMessage.getSender());

        messageFormat = messageUtil.replacePlaceholder(messageFormat, "{playerName}", chatMessage.getSender().getName());
        messageFormat = messageUtil.replacePlaceholder(messageFormat, "{message}", chatMessage.getContent());

        if (hoverFormat != null) {
            messageFormat = messageFormat.hoverEvent(HoverEvent.showText(hoverFormat));
        }

        return messageFormat;
    }

    private Component createHoverFormat(List<String> hoverFormatList, Player sender) {
        if (hoverFormatList == null || hoverFormatList.isEmpty()) {
            return null;
        }

        String hoverFormatString = String.join("\n", hoverFormatList);
        return formatterUtil.formatMessage(sender, hoverFormatString);
    }
}
