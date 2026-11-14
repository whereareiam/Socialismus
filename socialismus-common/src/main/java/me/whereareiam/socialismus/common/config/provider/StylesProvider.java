package me.whereareiam.socialismus.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.defaults.StylesDefaults;
import me.whereareiam.socialismus.model.style.StylesConfig;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class StylesProvider extends DefaultConfigProvider<StylesConfig> {
	@Inject
	public StylesProvider(@Named("dataPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, "styles", StylesConfig.class, registry);
	}

	@Override
	protected Configura configura() {
		return super.configura().withDefaults(StylesDefaults.class);
	}
}
