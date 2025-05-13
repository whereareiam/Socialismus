package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.Setter;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.output.LoggingHelper;

import java.util.logging.Logger;

public class BukkitLoggingHelper implements LoggingHelper {
    @Setter
    private static Logger logger;
    private final Provider<Settings> settings;

    @Inject
    public BukkitLoggingHelper(Provider<Settings> settings) {
        this.settings = settings;
    }

    @Override
    public void info(String message, Object... objects) {
        if (settings.get().getLevel() >= 2)
            logger.info(String.format(message, objects));
    }

    @Override
    public void warn(String message, Object... objects) {
        if (settings.get().getLevel() >= 1)
            logger.warning(String.format(message, objects));
    }

    @Override
    public void severe(String message, Object... objects) {
        if (settings.get().getLevel() >= 0)
            logger.severe(String.format(message, objects));
    }

    @Override
    public void debug(String message, Object... objects) {
        if (settings.get().getLevel() >= 3)
            logger.info(String.format(message, objects));
    }
}
