package me.whereareiam.socialismus.adapter.config.provider.base;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.adapter.config.management.DefaultObjectMapperRegistry;
import me.whereareiam.socialismus.api.output.config.ConfigurationTypeResolver;

import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ObjectMapperProvider implements Provider<ObjectMapper> {
	private final ConfigurationTypeResolver typeResolver;
	private final Set<Module> staticModules;
	private final DefaultObjectMapperRegistry registry;

	private ObjectMapper objectMapper;

	@Override
	public synchronized ObjectMapper get() {
		if (objectMapper != null) {
			return objectMapper;
		}

		switch (typeResolver.getConfigurationType()) {
			case JSON -> objectMapper = new JsonMapper();
			case YAML -> {
				YAMLFactory factory = new YAMLFactory()
						.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
						.enable(YAMLGenerator.Feature.INDENT_ARRAYS)
						.enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
						.disable(YAMLGenerator.Feature.SPLIT_LINES);
				objectMapper = new YAMLMapper(factory);
			}
			default -> throw new IllegalArgumentException("Unsupported configuration type");
		}

		for (Module m : staticModules) {
			objectMapper.registerModule(m);
		}

		registry.applyAllTo(objectMapper);

		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		return objectMapper;
	}
}
