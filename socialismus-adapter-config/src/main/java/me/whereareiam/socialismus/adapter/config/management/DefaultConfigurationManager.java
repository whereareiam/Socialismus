package me.whereareiam.socialismus.adapter.config.management;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;
import me.whereareiam.socialismus.api.output.config.ConfigurationTypeResolver;
import me.whereareiam.socialismus.api.type.ConfigurationType;

import java.util.HashMap;
import java.util.Map;

public class DefaultConfigurationManager implements ConfigurationManager {
	private final ConfigurationTypeResolver typeResolver;
	private final Map<Class<?>, DefaultConfig<?>> templates;

	@Inject
	public DefaultConfigurationManager(
			ConfigurationTypeResolver typeResolver,
			@Named("configTemplates") Map<Class<?>, DefaultConfig<?>> templates
	) {
		this.typeResolver = typeResolver;
		this.templates = new HashMap<>(templates);
	}

	@Override
	public ConfigurationType getConfigurationType() {
		return typeResolver.getConfigurationType();
	}

	@Override
	public void addTemplate(Class<?> clazz, DefaultConfig<?> template) {
		templates.put(clazz, template);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> DefaultConfig<T> getTemplate(Class<T> clazz) {
		return (DefaultConfig<T>) templates.get(clazz);
	}
}
