package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;

@Singleton
public class WorldPlayerUtil {
    @Inject
    public WorldPlayerUtil(LoggerUtil loggerUtil) {
        loggerUtil.trace("Initializing class: " + this);
    }

    @Cacheable(duration = 1)
    public Collection<Player> getPlayersInWorld(World world) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().equals(world))
                .collect(Collectors.toList());
    }
}
