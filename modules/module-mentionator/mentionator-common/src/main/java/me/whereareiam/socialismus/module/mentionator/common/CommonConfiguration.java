package me.whereareiam.socialismus.module.mentionator.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.mentionator.api.MentionatorService;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorCommands;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorMessages;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.Notification;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.MentionatorCommandsProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.MentionatorMessagesProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.MentionatorPhrasesProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.MentionatorSettingsProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.NotificationsProvider;
import me.whereareiam.socialismus.module.mentionator.common.config.provider.TagsProvider;
import me.whereareiam.socialismus.module.mentionator.common.mode.DefaultMentionatorService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;

/**
 * Binds Mentionator's documents and services.
 */
@RequiredArgsConstructor
public class CommonConfiguration extends AbstractModule {
	private final Path workingPath;
	private final boolean dialogue;

	@Override
	protected void configure() {
		bind(Boolean.class).annotatedWith(Names.named("dialogue")).toInstance(dialogue);
		bind(Clock.class).toInstance(Clock.systemUTC());

		bind(MentionatorSettingsProvider.class).asEagerSingleton();
		bind(MentionatorSettings.class).toProvider(MentionatorSettingsProvider.class);

		bind(MentionatorMessagesProvider.class).asEagerSingleton();
		bind(MentionatorMessages.class).toProvider(MentionatorMessagesProvider.class);

		bind(MentionatorCommandsProvider.class).asEagerSingleton();
		bind(MentionatorCommands.class).toProvider(MentionatorCommandsProvider.class);

		bind(MentionatorPhrasesProvider.class).asEagerSingleton();

		bind(TagsProvider.class).asEagerSingleton();
		bind(new TypeLiteral<List<Tag>>() {}).toProvider(TagsProvider.class);

		bind(NotificationsProvider.class).asEagerSingleton();
		bind(new TypeLiteral<List<Notification>>() {}).toProvider(NotificationsProvider.class);

		bind(MentionatorService.class).to(DefaultMentionatorService.class);
	}

	@Provides
	@Singleton
	@Named("workingPath")
	Path provideWorkingPath() {
		return directory(workingPath);
	}

	@Provides
	@Singleton
	@Named("tagsPath")
	Path provideTagsPath() {
		return directory(workingPath.resolve("tags"));
	}

	@Provides
	@Singleton
	@Named("notificationsPath")
	Path provideNotificationsPath() {
		return directory(workingPath.resolve("notifications"));
	}

	private static Path directory(Path path) {
		try {
			return Files.createDirectories(path);
		} catch (IOException failure) {
			throw new UncheckedIOException("Mentionator cannot create " + path, failure);
		}
	}
}
