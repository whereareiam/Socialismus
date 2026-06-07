package me.whereareiam.socialismus.common.config.provider.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatDefaults;
import me.whereareiam.socialismus.common.config.dynamic.ChatsConfig;
import me.whereareiam.socialismus.common.config.provider.DefaultConfigProvider;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.registry.base.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Singleton
public class ChatsProvider extends DefaultConfigProvider<List<Chat>> {
	@Inject
	public ChatsProvider(
			@Named("chatPath") Path dataPath,
			Registry<Reloadable> registry
	) {
		super(dataPath, "", listType(), registry);
	}

	@Override
	protected List<Chat> load() {
		List<Chat> loaded = new ArrayList<>();
		try (Stream<Path> paths = Files.list(getPath())) {
			paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
				String fileName = path.getFileName().toString();
				int dotIndex = fileName.lastIndexOf('.');
				String baseName = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);

				if (baseName.startsWith("messages") || baseName.startsWith("settings") || baseName.isEmpty()) return;

				loaded.addAll(addChatsFromConfig(path.getParent().resolve(baseName)));
			});
		} catch (IOException e) {
			Logger.severe("Failed to load chat configurations", e);
			return Collections.emptyList();
		}

		if (loaded.isEmpty()) loaded.addAll(addChatsFromConfig(getPath().resolve("chats-default")));

		loaded.removeIf(chat -> loaded.stream().anyMatch(c -> c != chat && c.getId().equals(chat.getId())));
		return loaded;
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(ChatDefaults.class), ChatsConfig.class);
	}

	private List<Chat> addChatsFromConfig(Path path) {
		ChatsConfig chatsConfig = configura().update(path, ChatsConfig.class);
		return chatsConfig.getChats().stream()
				.filter(Chat::isEnabled)
				.toList();
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends List<Chat>> listType() {
		return (Class<? extends List<Chat>>) List.class;
	}
}
