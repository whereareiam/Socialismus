package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Singleton
public class DistanceCalculatorUtil {
    @Inject
    public DistanceCalculatorUtil(LoggerUtil loggerUtil) {
        loggerUtil.trace("Initializing class: " + this);
    }

    public static double calculateDistance(Player player1, Player player2) {
        if (!player1.getWorld().equals(player2.getWorld())) {
            return -1;
        }
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();
        return loc1.distance(loc2);
    }
}

