package me.whereareiam.socialismus.common.config;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import me.whereareiam.strata.Strata;
import org.jetbrains.annotations.NotNull;

/**
 * Loads the configuration of Socialismus: brings the documents of an older installation up to date through
 * {@link me.whereareiam.socialismus.common.config.migration.SocialismusConfigMigrations}, before any document is read,
 * installs the shared Configura instance and loads every bound document in the order it was bound, which is also
 * the order {@code /socialismus reload} reloads them in. A failed migration stops the startup, so Socialismus does
 * not run on a directory it could not upgrade.
 */
public final class ConfigurationLifecycle implements RuntimeLifecycle {
	private final Injector injector;
	private final Provider<Configura> configura;
	private final Provider<Strata> strata;

	@Inject
	public ConfigurationLifecycle(@NotNull Injector injector, @NotNull Provider<Configura> configura, @NotNull Provider<Strata> strata) {
		this.injector = injector;
		this.configura = configura;
		this.strata = strata;
	}

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.CONFIGURATION;
	}

	@Override
	public void initialize() {
		strata.get().migrate();
		Config.setConfigured(configura.get());

		for (Key<?> key : injector.getAllBindings().keySet())
			if (ConfigProvider.class.isAssignableFrom(key.getTypeLiteral().getRawType()))
				((ConfigProvider<?>) injector.getInstance(key)).get();
	}

	/**
	 * Keeps the configuration installed: what stops after this lifecycle may still read its documents.
	 */
	@Override
	public void shutdown() {
	}
}
