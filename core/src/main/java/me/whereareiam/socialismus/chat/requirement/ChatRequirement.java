package me.whereareiam.socialismus.chat.requirement;

import me.whereareiam.socialismus.chat.Chat;
import org.bukkit.entity.Player;

public interface ChatRequirement {
    boolean checkRequirement(Player player, Chat chat);
}