package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.dialogue.api.ConversationStore;
import me.whereareiam.socialismus.module.dialogue.api.DialogueService;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.MessageControlService;
import me.whereareiam.socialismus.module.dialogue.api.MessageModerationService;
import me.whereareiam.socialismus.module.dialogue.api.MessageToggleStore;
import me.whereareiam.socialismus.module.dialogue.api.SpyStore;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueCommands;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSync;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.api.model.config.ModerationFormats;
import me.whereareiam.socialismus.module.dialogue.common.config.provider.DialogueCommandsProvider;
import me.whereareiam.socialismus.module.dialogue.common.config.provider.DialogueMessagesProvider;
import me.whereareiam.socialismus.module.dialogue.common.config.provider.DialogueSettingsProvider;
import me.whereareiam.socialismus.module.dialogue.common.config.provider.DialogueSyncProvider;
import me.whereareiam.socialismus.module.dialogue.common.config.provider.MessagingFormatsProvider;
import me.whereareiam.socialismus.module.dialogue.common.config.provider.ModerationFormatsProvider;
import me.whereareiam.socialismus.module.dialogue.common.control.DefaultMessageControlService;
import me.whereareiam.socialismus.module.dialogue.common.control.InMemoryIgnoreStore;
import me.whereareiam.socialismus.module.dialogue.common.control.InMemoryMessageToggleStore;
import me.whereareiam.socialismus.module.dialogue.common.conversation.InMemoryConversationStore;
import me.whereareiam.socialismus.module.dialogue.common.moderation.DefaultMessageModerationService;
import me.whereareiam.socialismus.module.dialogue.common.moderation.InMemorySpyStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class CommonConfiguration extends AbstractModule {
	private final Path workingPath;

	@Override
	protected void configure() {
		// Configuration
		bind(DialogueSettingsProvider.class).asEagerSingleton();
		bind(DialogueSettings.class).toProvider(DialogueSettingsProvider.class);

		bind(DialogueMessagesProvider.class).asEagerSingleton();
		bind(DialogueMessages.class).toProvider(DialogueMessagesProvider.class);

		bind(DialogueCommandsProvider.class).asEagerSingleton();
		bind(DialogueCommands.class).toProvider(DialogueCommandsProvider.class);

		bind(DialogueSyncProvider.class).asEagerSingleton();
		bind(DialogueSync.class).toProvider(DialogueSyncProvider.class);

		bind(MessagingFormatsProvider.class).asEagerSingleton();
		bind(MessagingFormats.class).toProvider(MessagingFormatsProvider.class);

		bind(ModerationFormatsProvider.class).asEagerSingleton();
		bind(ModerationFormats.class).toProvider(ModerationFormatsProvider.class);

		// General
		bind(DialogueService.class).to(DefaultDialogueService.class);
		bind(ConversationStore.class).to(InMemoryConversationStore.class);
		bind(MessageToggleStore.class).to(InMemoryMessageToggleStore.class);
		bind(IgnoreStore.class).to(InMemoryIgnoreStore.class);
		bind(MessageControlService.class).to(DefaultMessageControlService.class);
		bind(SpyStore.class).to(InMemorySpyStore.class);
		bind(MessageModerationService.class).to(DefaultMessageModerationService.class);
	}

	@Provides
	@Singleton
	@Named("workingPath")
	Path provideWorkingPath() {
		return ensureDirectory(workingPath, "workingPath");
	}

	@Provides
	@Singleton
	@Named("formatsPath")
	Path provideFormatsPath() {
		return ensureDirectory(workingPath.resolve("formats"), "formatsPath");
	}

	private Path ensureDirectory(Path path, String label) {
		try {
			Files.createDirectories(path);
			return path;
		} catch (IOException e) {
			throw new RuntimeException("Failed to create " + label + " directory", e);
		}
	}
}
