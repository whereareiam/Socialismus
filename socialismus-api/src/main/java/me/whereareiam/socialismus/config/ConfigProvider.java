package me.whereareiam.socialismus.config;

import com.google.inject.Provider;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.migration.MigrationDefinition;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Reusable runner for configuration providers.
 */
public abstract class ConfigProvider<T> implements Provider<T>, Reloadable {
	private final Path path;
	private final Class<? extends T> type;
	private T value;

	protected ConfigProvider(
			Path basePath,
			String fileName,
			Class<? extends T> type,
			Registry<Reloadable> reloadables
	) {
		this.path = basePath.resolve(fileName);
		this.type = type;
		reloadables.register(this);
	}

	@Override
	public T get() {
		if (value != null) return value;

		value = load();
		return value;
	}

	@Override
	public void reload() {
		value = load();
	}

	protected final Path getPath() {
		return path;
	}

	protected T load() {
		return configura().update(path, resolveType(path));
	}

	protected Class<? extends T> resolveType(Path path) {
		return type;
	}

	protected final <R> R read(Path path, Class<R> type) {
		return configura().read(path, type);
	}

	protected Configura configura() {
		return Config.configured();
	}

	protected final <C> Configura versioned(Configura configura, Class<C> type) {
		return versioned(configura, type, spec -> spec
				.currentVersion(1)
				.assumeVersionWhenMissing(1));
	}

	protected final <C> Configura versioned(
			Configura configura,
			Class<C> type,
			Consumer<MigrationDefinition<C>> customizer
	) {
		if (configura == null || type == null || configura.isVersioned(type))
			return configura;

		return configura.withVersioned(type, customizer);
	}
}

