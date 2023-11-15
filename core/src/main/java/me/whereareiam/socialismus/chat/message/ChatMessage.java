package me.whereareiam.socialismus.chat.message;

import me.whereareiam.socialismus.model.Chat;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ChatMessage {
    private Player sender;
    private Component content;
    private Chat chat;

    public ChatMessage(Player sender, Component content, Chat chat) {
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

    public Component getContent() {
        return content;
    }

    public void setContent(Component content) {
        this.content = content;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
