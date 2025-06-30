package me.whereareiam.socialismus.adapter.config.provider.chat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.adapter.config.management.DefaultConfigurationLoader;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;

import java.nio.file.Path;

@Singleton
public class ChatSettingsProvider implements Provider<ChatSettings>, Reloadable {
	private final Path dataPath;
	private final DefaultConfigurationLoader defaultConfigurationLoader;

	private ChatSettings chatSettings;

	@Inject
	public ChatSettingsProvider(@Named("chatPath") Path dataPath, DefaultConfigurationLoader defaultConfigurationLoader, Registry<Reloadable> registry) {
		this.dataPath = dataPath;
		this.defaultConfigurationLoader = defaultConfigurationLoader;

		registry.register(this);
	}

	@Override
	public ChatSettings get() {
		if (chatSettings != null) return chatSettings;

		load();

		return chatSettings;
	}

	@Override
	public void reload() {
		load();
	}

	private void load() {
		chatSettings = defaultConfigurationLoader.load(dataPath.resolve("settings"), ChatSettings.class);
	}
}