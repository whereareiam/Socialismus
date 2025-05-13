package me.whereareiam.socialismus.platform.paper.util;

import org.bukkit.event.EventPriority;

public class PaperUtil {
    public static EventPriority of(me.whereareiam.socialismus.api.type.EventPriority priority) {
        return switch (priority) {
            case LOWEST -> EventPriority.LOWEST;
            case LOW -> EventPriority.LOW;
            case NORMAL -> EventPriority.NORMAL;
            case HIGH -> EventPriority.HIGH;
            case HIGHEST -> EventPriority.HIGHEST;
        };
    }
}
