package me.whereareiam.socialismus.platform.velocity.util;

import me.whereareiam.socialismus.type.EventPriority;

public class VelocityUtil {
    public static short of(EventPriority priority) {
        return switch (priority) {
            case LOWEST -> Short.MIN_VALUE;
            case LOW -> Short.MIN_VALUE / 2;
            case NORMAL -> 0;
            case HIGH -> Short.MAX_VALUE / 2;
            case HIGHEST -> Short.MAX_VALUE;
        };
    }
}
