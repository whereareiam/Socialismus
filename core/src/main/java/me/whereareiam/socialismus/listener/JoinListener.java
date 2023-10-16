package me.whereareiam.socialismus.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface JoinListener extends Listener {
    void onPlayerJoinEvent(Player player);
}
