package me.whereareiam.socialismus.module.chirper.common;

import com.google.inject.Provider;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.module.chirper.api.AnnouncementBroadcaster;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;
import me.whereareiam.socialismus.module.chirper.api.model.announcer.Announcer;
import me.whereareiam.socialismus.module.chirper.api.type.OrderType;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncerControllerTest {
	@Mock
	private Registry<Reloadable> reloadableRegistry;

	@Mock
	private Scheduler scheduler;

	@Mock
	private AnnouncementBroadcaster broadcaster;

	@Mock
	private PlatformInteractor interactor;

	@BeforeEach
	void setUp() {
		Logger.init(new LoggingHelper() {
			@Override
			public void info(String message, Object... objects) {
			}

			@Override
			public void warn(String message, Object... objects) {
			}

			@Override
			public void severe(String message, Object... objects) {
			}

			@Override
			public void debug(String message, Object... objects) {
			}
		});
	}

	@Test
	void initSchedulesEachAnnouncerOnlyOnce() {
		Announcer firstAnnouncer = announcer("first");
		Announcer secondAnnouncer = announcer("second");

		AnnouncerController controller = new AnnouncerController(
				reloadableRegistry,
				scheduler,
				announcerProvider(List.of(firstAnnouncer, secondAnnouncer)),
				announcementProvider(List.of(announcement("first"), announcement("second"))),
				broadcaster,
				interactor
		);

		controller.init();

		verify(scheduler, times(2)).schedule(any(PeriodicalRunnableTask.class), eq(true));
	}

	@Test
	void initSchedulesAscendingAnnouncementsInNaturalOrder() {
		AnnouncerController controller = new AnnouncerController(
				reloadableRegistry,
				scheduler,
				announcerProvider(List.of(announcer(List.of("bravo", "alpha"), OrderType.ASCENDING))),
				announcementProvider(List.of(announcement("bravo"), announcement("alpha"))),
				broadcaster,
				interactor
		);
		when(interactor.getOnlinePlayersCount()).thenReturn(1);

		controller.init();

		ArgumentCaptor<PeriodicalRunnableTask> taskCaptor = ArgumentCaptor.forClass(PeriodicalRunnableTask.class);
		verify(scheduler).schedule(taskCaptor.capture(), eq(true));

		taskCaptor.getValue().getRunnable().run();

		verify(broadcaster).broadcast(argThat(capturedAnnouncement -> capturedAnnouncement.getId().equals("alpha")));
	}

	@Test
	void initSchedulesDescendingAnnouncementsInReverseOrder() {
		AnnouncerController controller = new AnnouncerController(
				reloadableRegistry,
				scheduler,
				announcerProvider(List.of(announcer(List.of("alpha", "bravo"), OrderType.DESCENDING))),
				announcementProvider(List.of(announcement("alpha"), announcement("bravo"))),
				broadcaster,
				interactor
		);
		when(interactor.getOnlinePlayersCount()).thenReturn(1);

		controller.init();

		ArgumentCaptor<PeriodicalRunnableTask> taskCaptor = ArgumentCaptor.forClass(PeriodicalRunnableTask.class);
		verify(scheduler).schedule(taskCaptor.capture(), eq(true));

		taskCaptor.getValue().getRunnable().run();

		verify(broadcaster).broadcast(argThat(capturedAnnouncement -> capturedAnnouncement.getId().equals("bravo")));
	}

	private static Provider<List<Announcer>> announcerProvider(List<Announcer> announcers) {
		return () -> announcers;
	}

	private static Provider<List<Announcement>> announcementProvider(List<Announcement> announcements) {
		return () -> announcements;
	}

	private static Announcer announcer(String announcementId) {
		return announcer(List.of(announcementId), OrderType.ASCENDING);
	}

	private static Announcer announcer(List<String> announcementIds, OrderType orderType) {
		return Announcer.builder()
				.enabled(true)
				.announcements(announcementIds)
				.settings(Announcer.AnnouncerSettings.builder()
						.order(orderType)
						.minPlayers(0)
						.interval(30)
						.delay(0)
						.build())
				.build();
	}

	private static Announcement announcement(String id) {
		return Announcement.builder()
				.enabled(true)
				.id(id)
				.settings(Announcement.AnnouncementSettings.builder()
						.delay(0)
						.repeat(true)
						.build())
				.build();
	}
}
