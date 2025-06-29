package me.whereareiam.socialismus.adapter.config.management;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.adapter.config.deserializer.RequirementDeserializer;
import me.whereareiam.socialismus.adapter.config.deserializer.VersionDeserializer;
import me.whereareiam.socialismus.api.model.requirement.Requirement;
import me.whereareiam.socialismus.api.output.DefaultConfig;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;
import me.whereareiam.socialismus.api.type.ConfigurationType;
import me.whereareiam.socialismus.api.type.Version;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Singleton
@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigManager implements Provider<ObjectMapper>, ConfigurationManager {
	private final Injector injector;
	private final Path dataPath;

	private final Map<Class<?>, DefaultConfig<?>> templates = new HashMap<>();

	private ObjectMapper objectMapper;

	@Inject
	public ConfigManager(
			Injector injector,
			@Named("dataPath") Path dataPath,
			Map<Class<?>, DefaultConfig<?>> templates
	) {
		this.injector = injector;
		this.dataPath = dataPath;
		this.templates.putAll(templates);
	}

	@Override
	public ConfigurationType getConfigurationType() {
		try (Stream<Path> paths = Files.list(this.dataPath)) {
			Optional<Path> configFile = paths
					.filter(Files::isRegularFile)
					.filter(file -> file.getFileName().toString().startsWith("type"))
					.findFirst();

			if (configFile.isPresent()) {
				String[] parts = configFile.get().getFileName().toString().split("=");

				return ConfigurationType.valueOf(parts[1].toUpperCase());
			} else {
				Files.createFile(this.dataPath.resolve("type=YAML"));

				return ConfigurationType.YAML;
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to get configuration type", e);
		}
	}

	@Override
	public void addDeserializer(Class<?> clazz, Object deserializer) {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(clazz, (JsonDeserializer) deserializer);
		objectMapper.registerModule(module);
	}

	@Override
	public void addSerializer(Class<?> clazz, Object serializer) {
		SimpleModule module = new SimpleModule();
		module.addSerializer(clazz, (JsonSerializer) serializer);
		objectMapper.registerModule(module);
	}

	@Override
	public void addTemplate(Class<?> clazz, DefaultConfig<?> template) {
		templates.put(clazz, template);
	}

	@Override
	public <T> DefaultConfig<T> getTemplate(Class<T> clazz) {
		return (DefaultConfig<T>) templates.get(clazz);
	}

	@Override
	public ObjectMapper get() {
		if (objectMapper != null) return objectMapper;

		switch (getConfigurationType()) {
			case JSON -> objectMapper = new JsonMapper();
			case YAML -> {
				YAMLFactory yamlFactory = new YAMLFactory()
						.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
						.enable(YAMLGenerator.Feature.INDENT_ARRAYS)
						.enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
						.disable(YAMLGenerator.Feature.SPLIT_LINES);
				objectMapper = new YAMLMapper(yamlFactory);
			}

			default -> throw new IllegalArgumentException("Unsupported configuration type");
		}

		addDeserializer(Requirement.class, injector.getInstance(RequirementDeserializer.class));
		addDeserializer(Version.class, injector.getInstance(VersionDeserializer.class));

		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

		return objectMapper;
	}
}
