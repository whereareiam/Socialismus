package me.whereareiam.socialismus.output.config;

import me.whereareiam.socialismus.Reloadable;

/**
 * Reusable base for configuration providers.
 * Provides lazy get(), reload(), template registration, and post-load hooks.
 * <p>
 * API consumers can extend this and implement {@link #load()}.
 */
public abstract class ConfigProvider<T> implements Reloadable {
	private T value;
	private boolean templatesRegistered;

	public T get() {
		if (value != null) return value;

		ensureTemplatesRegistered();
		value = load();
		return value;
	}

	@Override
	public void reload() {
		ensureTemplatesRegistered();
		value = load();
	}

	private void ensureTemplatesRegistered() {
		if (templatesRegistered) return;
		registerTemplate();
		templatesRegistered = true;
	}

	/**
	 * Implementations must load the configuration object (e.g., from disk).
	 */
	protected abstract T load();

	/**
	 * Override to register templates before the first load. Default is no-op.
	 */
	protected void registerTemplate() {
	}
}


