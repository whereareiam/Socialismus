package me.whereareiam.socialismus.util.platform;

public enum PlatformType {
    SPIGOT,
    PAPER,
    FOLIA,
    UNKNOWN;

    public static PlatformType getCurrentPlatform() {
        if (Platform.isFolia)
            return FOLIA;
        if (Platform.isPaper)
            return PAPER;
        if (Platform.isSpigot)
            return SPIGOT;

        return UNKNOWN;
    }
}