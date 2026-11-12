package me.whereareiam.socialismus.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.defaults.PhrasesDefaults;
import me.whereareiam.socialismus.config.PhrasesConfigProvider;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class PhrasesProvider extends PhrasesConfigProvider {
	@Inject
	public PhrasesProvider(@Named("dataPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, registry, PhrasesDefaults.PHRASES);
	}
}
