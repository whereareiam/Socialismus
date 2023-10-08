package me.whereareiam.socialismus.feature.chat.requirement;

import me.whereareiam.socialismus.feature.chat.Chat;
import org.bukkit.entity.Player;

public interface ChatRequirement {
    boolean checkRequirement(Player player, Chat chat);
}