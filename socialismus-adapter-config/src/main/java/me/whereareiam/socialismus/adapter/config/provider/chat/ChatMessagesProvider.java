package me.whereareiam.socialismus.adapter.config.provider.chat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.adapter.config.management.DefaultConfigurationLoader;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.model.chat.ChatMessages;

import java.nio.file.Path;

@Singleton
public class ChatMessagesProvider implements Provider<ChatMessages>, Reloadable {
	private final Path dataPath;
	private final DefaultConfigurationLoader defaultConfigurationLoader;
	private ChatMessages chatMessages;

	@Inject
	public ChatMessagesProvider(@Named("chatPath") Path dataPath, DefaultConfigurationLoader defaultConfigurationLoader, Registry<Reloadable> registry) {
		this.dataPath = dataPath;
		this.defaultConfigurationLoader = defaultConfigurationLoader;

		registry.register(this);
	}

	@Override
	public ChatMessages get() {
		if (chatMessages != null) return chatMessages;

		load();

		return chatMessages;
	}

	@Override
	public void reload() {
		load();
	}

	private void load() {
		chatMessages = defaultConfigurationLoader.load(dataPath.resolve("messages"), ChatMessages.class);
	}
}