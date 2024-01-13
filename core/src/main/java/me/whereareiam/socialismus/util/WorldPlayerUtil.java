package me.whereareiam.socialismus.util;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

@Singleton
public class WorldPlayerUtil {
    public static Collection<Player> getPlayersInWorld(String world) {
        return getPlayersInWorld(Bukkit.getWorld(world));
    }

    @Cacheable(duration = 1)
    public static Collection<Player> getPlayersInWorld(World world) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().equals(world))
                .collect(Collectors.toList());
    }
}
