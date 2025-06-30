package me.whereareiam.socialismus.adapter.config.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.exception.ConfigLoadException;
import me.whereareiam.socialismus.api.output.config.ConfigurationLoader;
import me.whereareiam.socialismus.api.output.config.ConfigurationManager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

@Getter
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class DefaultConfigurationLoader implements ConfigurationLoader {
	private final ConfigurationManager configManager;
	private final ObjectMapper objectMapper;

	private final DefaultConfigurationSaver defaultConfigurationSaver;
	private final DefaultConfigurationMerger defaultConfigurationMerger;

	@SuppressWarnings("unchecked")
	public <T> T load(Path path, Class<T> clazz) {
		path = path.resolveSibling(path.getFileName() + configManager.getConfigurationType().getExtension());

		T config;
		try {
			config = objectMapper.readValue(path.toFile(), clazz);
		} catch (FileNotFoundException e) {
			config = configManager.getTemplate(clazz).getDefault();
			defaultConfigurationSaver.save(path, config);
		} catch (Exception e) {
			throw new ConfigLoadException("Failed to load configuration", e);
		}

		T defaultConfig = configManager.getTemplate(clazz).getDefault();
		defaultConfigurationMerger.merge(config, defaultConfig);
		defaultConfigurationSaver.save(path, config);

		return config;
	}

	@Override
	public <T> T load(InputStream stream, Class<T> clazz) {
		T config;
		try {
			config = objectMapper.readValue(stream, clazz);
		} catch (Exception e) {
			throw new ConfigLoadException("Failed to load configuration", e);
		}

		return config;
	}
}