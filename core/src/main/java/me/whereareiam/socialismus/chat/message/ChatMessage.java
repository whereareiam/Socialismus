package me.whereareiam.socialismus.chat.message;

import me.whereareiam.socialismus.chat.model.Chat;
import org.bukkit.entity.Player;

public class ChatMessage {
    private Player sender;
    private String content;
    private Chat chat;

    public ChatMessage(Player sender, String content, Chat chat) {
        this.sender = sender;
        this.content = content;
        this.chat = chat;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
