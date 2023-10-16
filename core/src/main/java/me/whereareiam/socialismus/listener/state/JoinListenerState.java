package me.whereareiam.socialismus.listener.state;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.util.LoggerUtil;

@Singleton
public class JoinListenerState {
    private final LoggerUtil loggerUtil;
    private boolean joinListenerRequired = false;

    @Inject
    public JoinListenerState(LoggerUtil loggerUtil) {
        this.loggerUtil = loggerUtil;

        loggerUtil.trace("Initializing class: " + this);
    }

    public boolean isJoinListenerRequired() {
        loggerUtil.debug("isJoinListenerRequired: " + joinListenerRequired);
        return joinListenerRequired;
    }

    public void setJoinListenerRequired(boolean state) {
        loggerUtil.debug("setJoinListenerRequired: " + state);
        this.joinListenerRequired = state;
    }
}
