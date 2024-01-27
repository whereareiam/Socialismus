package me.whereareiam.socialismus.core.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

public class WorldPlayerUtil {
	public static Collection<Player> getPlayersInWorld(String world) {
		return getPlayersInWorld(Bukkit.getWorld(world));
	}

	public static Collection<Player> getPlayersInWorld(World world) {
		return Bukkit.getOnlinePlayers().stream()
				.filter(player -> player.getWorld().equals(world))
				.collect(Collectors.toList());
	}
}
