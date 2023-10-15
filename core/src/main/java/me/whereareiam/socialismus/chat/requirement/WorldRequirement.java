package me.whereareiam.socialismus.chat.requirement;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.model.Chat;
import org.bukkit.entity.Player;

@Singleton
public class WorldRequirement implements ChatRequirement {
    @Override
    public boolean checkRequirement(Player player, Chat chat) {
        return chat.requirements.worlds == null
                || chat.requirements.worlds.isEmpty()
                || chat.requirements.worlds.contains(player.getWorld().getName());
    }
}