package me.whereareiam.socialismus.platform;

import com.google.inject.Inject;
import me.whereareiam.socialismus.util.LoggerUtil;

public class Platform {
    public static boolean isFolia = PlatformIdentifier.isFolia();
    public static boolean isPaper = PlatformIdentifier.isPaper();
    public static boolean isSpigot = PlatformIdentifier.isSpigot();
    @Inject
    public Platform(LoggerUtil loggerUtil) {
        loggerUtil.trace("Initializing class: " + this);
    }
}