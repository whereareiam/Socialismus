package me.whereareiam.socialismus.chat.message;

import me.whereareiam.socialismus.chat.Chat;
import org.bukkit.entity.Player;

public record ChatMessage(Player sender, String content, Chat chat) {
}
