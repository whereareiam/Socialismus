package me.whereareiam.socialismus.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.util.LoggerUtil;

@Singleton
public class TabCompleteService {
    private final LoggerUtil loggerUtil;
    private boolean tabCompleteListenerRequired = false;

    @Inject
    public TabCompleteService(LoggerUtil loggerUtil) {
        this.loggerUtil = loggerUtil;

        loggerUtil.trace("Initializing class: " + this);
    }

    public boolean isTabCompleteListenerRequired() {
        loggerUtil.debug("isChatListenerRequired: " + tabCompleteListenerRequired);
        return tabCompleteListenerRequired;
    }

    public void setTabCompleteListenerRequired(boolean state) {
        loggerUtil.debug("setChatListenerRequired: " + state);
        this.tabCompleteListenerRequired = state;
    }
}
