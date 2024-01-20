package me.whereareiam.socialismus.core.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;

public interface ChatListener extends Listener {
	boolean onPlayerChatEvent(Player player, Collection<? extends Player> recipients, String message);
}
