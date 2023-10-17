package me.whereareiam.socialismus.platform;

public enum PlatformType {
    SPIGOT,
    PAPER,
    FOLIA,
    UNKNOWN;
    
    public static PlatformType getCurrentPlatform() {
        if (PlatformIdentifier.isFolia())
            return FOLIA;
        if (PlatformIdentifier.isPaper())
            return PAPER;
        if (PlatformIdentifier.isSpigot())
            return SPIGOT;

        return UNKNOWN;
    }
}