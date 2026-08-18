package me.whereareiam.socialismus.module.chirper.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.module.chirper.api.AnnouncementBroadcaster;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;
import me.whereareiam.socialismus.module.chirper.api.model.announcer.Announcer;
import me.whereareiam.socialismus.module.chirper.api.model.announcer.InternalAnnouncer;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class AnnouncerController implements Reloadable {
	private final Scheduler scheduler;
	private final Provider<List<Announcer>> announcerProvider;
	private final Provider<List<Announcement>> announcementProvider;
	private final AnnouncementBroadcaster broadcaster;
	private final PlatformInteractor interactor;

	private final Map<InternalAnnouncer, List<Announcement>> announcers = new HashMap<>();

	@Inject
	public AnnouncerController(
			Registry<Reloadable> reloadableRegistry,
			Scheduler scheduler,
			Provider<List<Announcer>> announcerProvider,
			Provider<List<Announcement>> announcementProvider,
			AnnouncementBroadcaster broadcaster,
			PlatformInteractor interactor
	) {
		this.scheduler = scheduler;
		this.announcerProvider = announcerProvider;
		this.announcementProvider = announcementProvider;
		this.broadcaster = broadcaster;
		this.interactor = interactor;

		reloadableRegistry.register(this);
	}

	public void init() {
		List<Announcer> announcersList = announcerProvider.get();
		List<Announcement> announcementsList = announcementProvider.get();

		Logger.debug("Loaded " + announcersList.size() + " announcers and " + announcementsList.size() + " announcements");

		for (Announcer announcer : announcersList) {
			if (announcer.getAnnouncements().isEmpty())
				continue;

			List<Announcement> filteredAnnouncements = announcementsList.stream()
					.filter(announcement -> announcer.getAnnouncements().contains(announcement.getId()))
					.collect(Collectors.toList());

			switch (announcer.getSettings().getOrder()) {
				case DESCENDING:
					filteredAnnouncements.sort(Comparator.comparing(Announcement::getId).reversed());
					break;
				case ASCENDING:
					filteredAnnouncements.sort(Comparator.comparing(Announcement::getId));
					break;
				case RANDOM:
					Collections.shuffle(filteredAnnouncements);
					break;
			}

			this.announcers.put(InternalAnnouncer.builder()
							.index(0)
							.enabled(announcer.isEnabled())
							.announcements(announcer.getAnnouncements())
							.settings(announcer.getSettings())
							.build(),
					filteredAnnouncements
			);
		}

		runAnnouncers();
	}

	private void runAnnouncers() {
		Random random = new Random();

		announcers.forEach((announcer, announcements) -> {
			long delay = announcer.getSettings().getDelay() * 1000L;
			long interval = announcer.getSettings().getInterval() * 1000L;
			int taskId = random.nextInt();

			Logger.debug("Scheduling task #" + taskId + " for announcer with delay=" + delay + "ms, interval=" + interval + "ms");

			scheduler.schedule(PeriodicalRunnableTask.builder()
							.id(taskId)
							.module("chirper")
							.runnable(() -> broadcastNextAnnouncement(announcer))
							.delay(delay)
							.period(interval)
							.build(),
					true
			);
		});
	}

	private void broadcastNextAnnouncement(InternalAnnouncer announcer) {
		Announcement nextAnnouncement = getNextAnnouncement(announcer);

		if (nextAnnouncement != null && announcer.getSettings().getMinPlayers() <= interactor.getOnlinePlayersCount()) {
			if (!nextAnnouncement.getSettings().isRepeat()) announcers.get(announcer).remove(nextAnnouncement);

			broadcaster.broadcast(nextAnnouncement);
		}
	}

	private Announcement getNextAnnouncement(InternalAnnouncer announcer) {
		List<Announcement> announcements = announcers.get(announcer);
		if (announcements == null || announcements.isEmpty())
			return null;

		int currentIndex = announcer.getIndex();
		Announcement nextAnnouncement = announcements.get(currentIndex);

		announcer.setIndex((currentIndex + 1) % announcements.size());

		return nextAnnouncement;
	}

	@Override
	public void reload() {
		announcers.clear();
		scheduler.cancelByModule("chirper");
		init();
	}
}
