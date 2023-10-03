package me.whereareiam.socialismus.feature.chat.message;

import me.whereareiam.socialismus.feature.chat.Chat;
import org.bukkit.entity.Player;

public record ChatMessage(Player sender, String content, Chat chat) {
}
