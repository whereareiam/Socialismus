package me.whereareiam.socialismus.chat.message;

import com.google.inject.Inject;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.module.chats.ChatManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

public class ChatMessageFactory {
    private final ChatManager chatManager;

    @Inject
    public ChatMessageFactory(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    public ChatMessage createChatMessage(Player sender, String message) {
        char chatSymbol = message.charAt(0);
        String symbol;

        if (!Character.isLetterOrDigit(chatSymbol)) {
            symbol = String.valueOf(chatSymbol);
            message = message.substring(1);
        } else {
            symbol = "";
        }

        Chat chat = chatManager.getChatBySymbol(symbol);

        if (chat == null && !symbol.isEmpty()) {
            message = symbol + message;
            symbol = "";
            chat = chatManager.getChatBySymbol(symbol);
        }

        Component content = PlainTextComponentSerializer.plainText().deserialize(message.trim());

        return new ChatMessage(sender, content, chat);
    }
}
