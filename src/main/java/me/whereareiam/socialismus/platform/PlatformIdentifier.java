package me.whereareiam.socialismus.platform;

public class PlatformIdentifier {
    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.ThreadedRegionizer");
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean isSpigot() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }
}