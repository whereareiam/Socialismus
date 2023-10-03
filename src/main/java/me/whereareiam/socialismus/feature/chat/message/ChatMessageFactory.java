package me.whereareiam.socialismus.feature.chat.message;

import com.google.inject.Inject;
import me.whereareiam.socialismus.feature.chat.Chat;
import me.whereareiam.socialismus.feature.chat.ChatManager;
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
        } else {
            symbol = "";
        }

        Chat chat = chatManager.getChatBySymbol(symbol);

        return new ChatMessage(sender, message, chat);
    }
}