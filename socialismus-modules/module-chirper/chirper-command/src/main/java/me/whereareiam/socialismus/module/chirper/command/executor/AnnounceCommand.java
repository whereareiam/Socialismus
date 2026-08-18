package me.whereareiam.socialismus.module.chirper.command.executor;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.commandant.annotation.Definition;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.module.chirper.api.AnnouncementBroadcaster;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;
import me.whereareiam.socialismus.module.chirper.api.model.config.ChirperMessages;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Singleton
public class AnnounceCommand {
	private final AnnouncementBroadcaster broadcaster;
	private final Provider<ChirperMessages> messages;
	private final Provider<List<Announcement>> announcements;

	@Inject
	public AnnounceCommand(
			@NotNull AnnouncementBroadcaster broadcaster,
			@NotNull Provider<ChirperMessages> messages,
			@NotNull Provider<List<Announcement>> announcements
	) {
		this.broadcaster = broadcaster;
		this.messages = messages;
		this.announcements = announcements;
	}

	@Definition("announce")
	@Command("socialismus announce <id> [simplified]")
	public void command(
			@NotNull Actor sender,
			@Argument("id") @NotNull String id,
			@Argument("simplified") @Default("false") boolean simplified
	) {
		Optional<Announcement> announcement = announcements.get().stream()
				.filter(a -> a.getId().equals(id))
				.findFirst();

		if (announcement.isEmpty()) {
			Component component = Serializer.serialize(SerializerContent.builder()
					.receiver(sender)
					.message(messages.get().getNoAnnouncementFound())
					.placeholder("id", id)
					.build());
			sender.sendMessage(component);
			return;
		}

		Component component = Serializer.serialize(SerializerContent.builder()
				.receiver(sender)
				.message(messages.get().getAnnouncementBroadcasted())
				.placeholder("id", id)
				.build());
		sender.sendMessage(component);

		broadcaster.broadcast(announcement.get(), simplified);
	}
}
