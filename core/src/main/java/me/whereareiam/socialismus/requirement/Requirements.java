package me.whereareiam.socialismus.requirement;

import com.google.inject.Singleton;

@Singleton
public enum Requirements {
    USE_PERMISSION(false),
    SEE_PERMISSION(true),
    WORLD(false),
    SYMBOL_COUNT(true);

    Requirements(boolean optional) {
    }
}
