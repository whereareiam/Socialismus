package me.whereareiam.socialismus.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DistanceCalculatorUtil {

    public static double calculateDistance(Player player1, Player player2) {
        if (!player1.getWorld().equals(player2.getWorld())) {
            return -1;
        }
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();
        return loc1.distance(loc2);
    }
}

