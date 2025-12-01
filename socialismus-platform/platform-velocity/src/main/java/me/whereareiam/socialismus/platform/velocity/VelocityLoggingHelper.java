package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.Setter;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.output.LoggingHelper;
import org.slf4j.Logger;

public class VelocityLoggingHelper implements LoggingHelper {
    @Setter
    private static Logger logger;
    private final Provider<Settings> settings;

    @Inject
    public VelocityLoggingHelper(Provider<Settings> settings) {
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
            logger.warn(String.format(message, objects));
    }

    @Override
    public void severe(String message, Object... objects) {
        if (settings.get().getLevel() >= 0)
            logger.error(String.format(message, objects));
    }

    @Override
    public void debug(String message, Object... objects) {
        if (settings.get().getLevel() >= 3)
            logger.info(String.format(message, objects));
    }
}
