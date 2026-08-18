package me.whereareiam.socialismus.module.chirper.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.module.chirper.api.AnnouncementBroadcaster;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;
import me.whereareiam.socialismus.module.chirper.api.model.announcer.Announcer;
import me.whereareiam.socialismus.module.chirper.api.model.config.ChirperCommands;
import me.whereareiam.socialismus.module.chirper.api.model.config.ChirperMessages;
import me.whereareiam.socialismus.module.chirper.common.broadcast.BroadcastCoordinator;
import me.whereareiam.socialismus.module.chirper.common.config.provider.AnnouncementsProvider;
import me.whereareiam.socialismus.module.chirper.common.config.provider.AnnouncersProvider;
import me.whereareiam.socialismus.module.chirper.common.config.provider.ChirperCommandsProvider;
import me.whereareiam.socialismus.module.chirper.common.config.provider.ChirperMessagesProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CommonConfiguration extends AbstractModule {
	private final Path workingPath;
	private final Path announcementsPath;
	private final Path announcersPath;

	public CommonConfiguration(Path workingDirectory) {
		this.workingPath = workingDirectory;
		this.announcementsPath = workingDirectory.resolve("announcements");
		this.announcersPath = workingDirectory.resolve("announcers");
	}

	@Override
	protected void configure() {
		requestInjection(this);

		// Configuration
		bind(AnnouncementsProvider.class).asEagerSingleton();
		bind(new TypeLiteral<List<Announcement>>() {}).toProvider(AnnouncementsProvider.class);

		bind(AnnouncersProvider.class).asEagerSingleton();
		bind(new TypeLiteral<List<Announcer>>() {}).toProvider(AnnouncersProvider.class);

		bind(ChirperCommandsProvider.class).asEagerSingleton();
		bind(ChirperCommands.class).toProvider(ChirperCommandsProvider.class);

		bind(ChirperMessagesProvider.class).asEagerSingleton();
		bind(ChirperMessages.class).toProvider(ChirperMessagesProvider.class);

		bind(AnnouncementBroadcaster.class).to(BroadcastCoordinator.class);
	}

	@Provides
	@Singleton
	@Named("workingPath")
	Path provideWorkingPath() {
		return ensureDirectory(workingPath, "workingPath");
	}

	@Provides
	@Singleton
	@Named("announcementsPath")
	Path provideAnnouncementsPath() {
		return ensureDirectory(announcementsPath, "announcementsPath");
	}

	@Provides
	@Singleton
	@Named("announcersPath")
	Path provideAnnouncersPath() {
		return ensureDirectory(announcersPath, "announcersPath");
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
