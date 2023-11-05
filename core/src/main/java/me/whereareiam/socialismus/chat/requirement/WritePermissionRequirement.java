package me.whereareiam.socialismus.chat.requirement;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.chat.Chat;
import org.bukkit.entity.Player;

@Singleton
public class WritePermissionRequirement implements ChatRequirement {
    @Override
    public boolean checkRequirement(Player player, Chat chat) {
        return chat.requirements.writePermission == null
                || chat.requirements.writePermission.isEmpty()
                || player.hasPermission(chat.requirements.writePermission);
    }
}
