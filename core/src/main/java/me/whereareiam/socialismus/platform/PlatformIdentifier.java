package me.whereareiam.socialismus.platform;

import com.google.inject.Singleton;

@Singleton
public class PlatformIdentifier {

    static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.ThreadedRegionizer");
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    static boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    static boolean isSpigot() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}