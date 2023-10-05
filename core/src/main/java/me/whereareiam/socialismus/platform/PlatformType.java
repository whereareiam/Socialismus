package me.whereareiam.socialismus.platform;

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