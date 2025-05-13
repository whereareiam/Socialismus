package me.whereareiam.socialismus.adapter.config.provider.chat;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.adapter.config.dynamic.ChatsConfig;
import me.whereareiam.socialismus.adapter.config.management.ConfigLoader;
import me.whereareiam.socialismus.adapter.config.management.ConfigManager;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.type.ConfigurationType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class ChatsProvider implements Provider<List<Chat>>, Reloadable {
	private final Path dataPath;
	private final ConfigLoader configLoader;
	private final ConfigurationType configurationType;
	private List<Chat> chats;

	@Inject
	public ChatsProvider(
			@Named("chatPath") Path dataPath,
			ConfigLoader configLoader,
			ConfigManager configManager,
			Registry<Reloadable> registry
	) {
		this.dataPath = dataPath;
		this.configLoader = configLoader;
		this.configurationType = configManager.getConfigurationType();

		registry.register(this);
	}

	@Override
	public List<Chat> get() {
		if (chats != null) return chats;

		loadChats();

		return chats;
	}

	@Override
	public void reload() {
		loadChats();
	}

	private void loadChats() {
		chats = new ArrayList<>();
		try (Stream<Path> paths = Files.list(dataPath)) {
			paths.filter(path -> path.getFileName().toString().endsWith(configurationType.getExtension())).forEach(path -> {
				String fileName = path.getFileName().toString().replace(configurationType.getExtension(), "");

				if (Files.isDirectory(path)
						|| fileName.startsWith("messages")
						|| fileName.startsWith("settings")
						|| fileName.isEmpty()
				) return;

				chats.addAll(addChatsFromConfig(path.getParent().resolve(fileName)));
			});
		} catch (IOException e) {
			Logger.severe("Failed to load chat configurations", e);
			chats = Collections.emptyList();
			return;
		}

		if (chats.isEmpty()) chats.addAll(addChatsFromConfig(dataPath.resolve("chats-default")));

		chats.removeIf(chat -> chats.stream().anyMatch(c -> c != chat && c.getId().equals(chat.getId())));
	}

	private List<Chat> addChatsFromConfig(Path path) {
		ChatsConfig chatsConfig = configLoader.load(path, ChatsConfig.class);
		return chatsConfig.getChats().stream()
				.filter(Chat::isEnabled)
				.toList();
	}
}