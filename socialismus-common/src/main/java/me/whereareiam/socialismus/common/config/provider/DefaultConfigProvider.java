package me.whereareiam.socialismus.common.config.provider;

import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

/**
 * Shared adapter-layer base for standard Socialismus configuration providers.
 */
public abstract class DefaultConfigProvider<T> extends ConfigProvider<T> {
	protected DefaultConfigProvider(
			Path basePath,
			String fileName,
			Class<? extends T> type,
			Registry<Reloadable> reloadables
	) {
		super(basePath, fileName, type, reloadables);
	}
}

