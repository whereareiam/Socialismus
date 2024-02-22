package me.whereareiam.socialismus.core.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class DistanceUtil {
	public static double between(Location a, Location b) {
		return a.getWorld() == b.getWorld() ? a.distance(b) : -1;
	}

	public static double between(Player a, Player b) {
		return between(a.getLocation(), b.getLocation());
	}
}

