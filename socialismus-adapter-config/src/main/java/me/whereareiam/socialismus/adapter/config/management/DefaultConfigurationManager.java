package me.whereareiam.socialismus.adapter.config.management;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;
import me.whereareiam.socialismus.api.output.config.ConfigurationTypeResolver;
import me.whereareiam.socialismus.api.type.ConfigurationType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DefaultConfigurationManager implements ConfigurationManager {
	private final ConfigurationTypeResolver typeResolver;

	private final Map<Class<?>, DefaultConfig<?>> templates = new ConcurrentHashMap<>();

	@Inject
	public DefaultConfigurationManager(
			ConfigurationTypeResolver typeResolver,
			@Named("configTemplates") Map<Class<?>, DefaultConfig<?>> bootstrap) {
		this.typeResolver = typeResolver;
		templates.putAll(bootstrap);
	}

	@Override
	public ConfigurationType getConfigurationType() {
		return typeResolver.getConfigurationType();
	}

	@Override
	public void addTemplate(Class<?> type, DefaultConfig<?> template) {
		System.out.println("Adding template for class: " + type.getName());
		templates.put(type, template);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> DefaultConfig<T> getTemplate(Class<T> type) {
		System.out.println(templates);
		System.out.println("Fetching template for class: " + type.getName());
		return (DefaultConfig<T>) templates.get(type);
	}
}
