package me.whereareiam.socialismus.common.logging;

import com.google.inject.Inject;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import org.jetbrains.annotations.NotNull;

/**
 * Connects the {@link Logger} of Socialismus to the platform's {@link LoggingHelper}, which writes to the server
 * console. It starts once the configuration is loaded, so the settings can name what is logged.
 */
public final class LoggingLifecycle implements RuntimeLifecycle {
	private final LoggingHelper loggingHelper;

	@Inject
	public LoggingLifecycle(@NotNull LoggingHelper loggingHelper) {
		this.loggingHelper = loggingHelper;
	}

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.LOGGING;
	}

	@Override
	public void initialize() {
		Logger.init(loggingHelper);
	}

	/**
	 * Keeps the log connected: what Socialismus reports while it finishes shutting down still reaches the console.
	 */
	@Override
	public void shutdown() {
	}
}
