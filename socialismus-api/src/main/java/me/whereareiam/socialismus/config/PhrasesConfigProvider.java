package me.whereareiam.socialismus.config;

import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

/**
 * Provides a {@code phrases} file: what an admin wrote, with the shipped phrases it lacks added and written back, so
 * a new phrase of an update appears in the file.
 */
public abstract class PhrasesConfigProvider extends ConfigProvider<Phrases> {
	private final Phrases defaults;

	protected PhrasesConfigProvider(Path basePath, Registry<Reloadable> reloadables, Phrases defaults) {
		super(basePath, "phrases", Phrases.class, reloadables);
		this.defaults = defaults;
	}

	@Override
	protected Phrases load() {
		Phrases written = configura().update(getPath(), Phrases.class);
		Phrases merged = written.withDefaults(defaults);
		if (!merged.equals(written)) configura().write(getPath(), merged);

		return merged;
	}
}
