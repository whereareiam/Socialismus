package me.whereareiam.socialismus.chat.message;

import com.google.inject.Inject;
import me.whereareiam.socialismus.chat.ChatUseType;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.module.chats.ChatManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ChatMessageFactory {
    private final ChatManager chatManager;

    @Inject
    public ChatMessageFactory(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    public ChatMessage createChatMessage(Player sender, String message, Optional<String> command) {
        char chatSymbol = message.charAt(0);
        String symbol;
        Chat chat = null;

        if (command.isPresent()) {
            chat = chatManager.getChatByCommand(command.get());
            if (chat.usage.type.equals(ChatUseType.SYMBOL))
                chat = null;
        }

        if (chat == null) {
            if (!Character.isLetterOrDigit(chatSymbol)) {
                symbol = String.valueOf(chatSymbol);
                message = message.substring(1);
                chat = chatManager.getChatBySymbol(symbol);
            }

            if (chat == null)
                chat = chatManager.getChatBySymbol("");
        }

        Component content = PlainTextComponentSerializer.plainText().deserialize(message.trim());

        return new ChatMessage(sender, content, chat);
    }
}
