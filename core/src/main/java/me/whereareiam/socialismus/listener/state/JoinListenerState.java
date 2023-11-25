package me.whereareiam.socialismus.listener.state;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.util.LoggerUtil;

@Singleton
public class JoinListenerState implements State {
    private static boolean isRequired = false;

    @Inject
    public JoinListenerState(LoggerUtil loggerUtil) {
        loggerUtil.trace("Initializing class: " + this);
    }

    public static boolean isRequired() {
        return isRequired;
    }

    public static void setRequired(boolean state) {
        isRequired = state;
    }
}
