package me.whereareiam.socialismus.core.listener;

import me.whereareiam.socialismus.api.model.ChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;

public interface ChatListener extends Listener {
	ChatMessage onPlayerChatEvent(Player player, Collection<? extends Player> recipients, String message);
}
