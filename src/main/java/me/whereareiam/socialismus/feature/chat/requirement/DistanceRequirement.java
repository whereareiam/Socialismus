package me.whereareiam.socialismus.feature.chat.requirement;

import me.whereareiam.socialismus.feature.chat.message.ChatMessage;
import me.whereareiam.socialismus.util.DistanceCalculatorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DistanceRequirement implements ChatRequirement {
    @Override
    public boolean checkRequirement(ChatMessage chatMessage) {
        if (chatMessage.chat().radius == -1) {
            return true;
        }
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!player.equals(chatMessage.sender())) {
                double distance = DistanceCalculatorUtil.calculateDistance(chatMessage.sender(), player);
                if (distance != -1 && distance <= chatMessage.chat().radius) {
                    return true;
                }
            }
        }
        return false;
    }
}