package me.whereareiam.socialismus.module.chirper.common.broadcast;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.chirper.api.AnnouncementBroadcaster;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.AnnouncementContent;
import me.whereareiam.socialismus.module.chirper.api.type.AnnouncementType;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BroadcastCoordinator implements AnnouncementBroadcaster {
    private final Scheduler scheduler;
    private final PlayerRegistry playerRegistry;
    private final BroadcastSender broadcastSender;
    private final RequirementEvaluatorService requirementEvaluator;

	private final Random random = new Random();

    @Override
    public void broadcast(Announcement announcement) {
        Collection<SocialismusPlayer> recipients = playerRegistry.getPlayers();
        recipients = recipients.parallelStream()
                .filter(r -> requirementEvaluator.check(announcement.getRequirements(), r))
                .collect(Collectors.toSet());

		if (announcement.getSettings().getDelay() > 0) {
			Collection<SocialismusPlayer> finalRecipients = recipients;
			scheduler.schedule(DelayedRunnableTask.builder()
					.id(random.nextInt())
					.module("chirper")
					.delay(announcement.getSettings().getDelay() * 1000L)
					.runnable(() -> sendAnnouncement(announcement, finalRecipients))
					.build());

			return;
		}

        sendAnnouncement(announcement, recipients);
    }

    @Override
    public void broadcast(Announcement announcement, boolean simplified) {
        if (!simplified) {
            broadcast(announcement);
            return;
        }

        Collection<SocialismusPlayer> recipients = playerRegistry.getPlayers();
        sendAnnouncement(announcement, recipients);
    }

	private void sendAnnouncement(Announcement announcement, Collection<SocialismusPlayer> recipients) {
		for (SocialismusPlayer recipient : recipients) {
			for (Map.Entry<AnnouncementType, ? extends AnnouncementContent> entry : announcement.getContents().entrySet())
				if (requirementEvaluator.check(entry.getValue().getRequirements(), recipient))
					broadcastSender.sendContent(entry.getKey(), entry.getValue(), recipient);
		}
	}
}
