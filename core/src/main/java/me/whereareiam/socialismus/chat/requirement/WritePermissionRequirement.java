package me.whereareiam.socialismus.chat.requirement;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.model.Chat;
import org.bukkit.entity.Player;

@Singleton
public class WritePermissionRequirement implements ChatRequirement {
    @Override
    public boolean checkRequirement(Player player, Chat chat) {
        return chat.writePermission == null || player.hasPermission(chat.writePermission);
    }
}
