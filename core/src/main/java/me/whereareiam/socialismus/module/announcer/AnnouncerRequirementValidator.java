package me.whereareiam.socialismus.module.announcer;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.announcement.AnnouncementRequirements;
import org.bukkit.entity.Player;

import java.util.Collection;

@Singleton
public class AnnouncerRequirementValidator {

    public Collection<? extends Player> filterPlayers(AnnouncementRequirements requirements, Collection<? extends Player> onlinePlayers) {
        if (!requirements.enabled)
            return onlinePlayers;

        if (requirements.permission != null && !requirements.permission.isEmpty())
            onlinePlayers.stream().filter(player -> !player.hasPermission(requirements.permission)).forEach(onlinePlayers::remove);

        return onlinePlayers;
    }
}