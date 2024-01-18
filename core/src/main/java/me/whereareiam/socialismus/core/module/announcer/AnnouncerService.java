package me.whereareiam.socialismus.core.module.announcer;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.announcement.Announcement;
import me.whereareiam.socialismus.api.model.announcer.Announcer;
import me.whereareiam.socialismus.core.Scheduler;
import me.whereareiam.socialismus.core.module.announcer.announcement.AnnouncementBroadcaster;
import me.whereareiam.socialismus.core.util.LoggerUtil;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class AnnouncerService {
	private final AnnouncerModule announcerModule;
	private final AnnouncementBroadcaster announcementBroadcaster;
	private final LoggerUtil loggerUtil;
	private final Scheduler scheduler;

	private final Map<Announcer, ScheduledFuture<?>> futures = new HashMap<>();
	private final Map<Announcer, Iterator<Announcement>> iterators = new HashMap<>();

	@Inject
	public AnnouncerService(AnnouncerModule announcerModule, AnnouncementBroadcaster announcementBroadcaster,
							LoggerUtil loggerUtil, Scheduler scheduler) {
		this.announcerModule = announcerModule;
		this.announcementBroadcaster = announcementBroadcaster;
		this.loggerUtil = loggerUtil;
		this.scheduler = scheduler;

		loggerUtil.trace("Initializing class: " + this);
	}

	public void startAnnouncers() {
		loggerUtil.debug("Starting announcers");
		Map<Announcer, List<Announcement>> announcers = announcerModule.getAnnouncers();
		for (Map.Entry<Announcer, List<Announcement>> entry : announcers.entrySet()) {
			Announcer announcer = entry.getKey();
			List<Announcement> announcements = entry.getValue();
			if (announcer.enabled) {
				Runnable task = getRunnable(announcer, announcements);
				ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(task, 0, announcer.interval, TimeUnit.SECONDS, Optional.empty());
				futures.put(announcer, future);
			}
		}
	}

	public void stopAnnouncers() {
		loggerUtil.debug("Stopping announcers");
		for (ScheduledFuture<?> future : futures.values()) {
			scheduler.cancelTask(future);
		}

		futures.clear();
		iterators.clear();
	}

	private Announcement selectAnnouncement(Announcer announcer, List<Announcement> announcements) {
		loggerUtil.trace("Selecting announcement for announcer: " + announcer.hashCode());
		switch (announcer.selectionType) {
			case RANDOM:
				int randomIndex = new Random().nextInt(announcements.size());
				Announcement randomAnnouncement = announcements.get(randomIndex);
				if (! randomAnnouncement.settings.repeat) {
					announcements.remove(randomIndex);
				}

				loggerUtil.trace("Selected announcement: " + randomAnnouncement.id);
				return randomAnnouncement;
			case SEQUENTIAL:
			default:
				Iterator<Announcement> iterator = iterators.get(announcer);
				if (iterator == null || ! iterator.hasNext()) {
					iterator = announcements.iterator();
					iterators.put(announcer, iterator);
				}
				if (iterator.hasNext()) {
					Announcement sequentialAnnouncement = iterator.next();
					if (! sequentialAnnouncement.settings.repeat) {
						iterator.remove();
					}

					loggerUtil.trace("Selected announcement: " + sequentialAnnouncement.id);
					return sequentialAnnouncement;
				}
		}

		return null;
	}

	private Runnable getRunnable(Announcer announcer, List<Announcement> announcements) {
		AtomicInteger delay = new AtomicInteger(0);
		return () -> {
			Announcement announcement = selectAnnouncement(announcer, announcements);
			if (announcement != null) {
				delay.set(announcement.settings.delay);
				scheduler.schedule(() -> announcementBroadcaster.postAnnouncement(announcement), delay.get(), TimeUnit.SECONDS);
			}
		};
	}
}