package me.whereareiam.socialismus.platform.common;

import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.model.scheduler.RunnableTask;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlatformSchedulerTest {
	private final RecordingScheduler scheduler = new RecordingScheduler();

	@Test
	void givesUnnumberedTasksDistinctIds() {
		RunnableTask first = new RunnableTask(0, "chirper", () -> {});
		RunnableTask second = new RunnableTask(0, "chirper", () -> {});

		scheduler.schedule(first);
		scheduler.schedule(second);

		assertTrue(first.getId() > 0);
		assertNotEquals(first.getId(), second.getId());
	}

	@Test
	void keepsTheIdOfANumberedTask() {
		RunnableTask task = new RunnableTask(42, "chirper", () -> {});

		scheduler.schedule(task);

		assertEquals(42, task.getId());
	}

	@Test
	void startsTasksSynchronouslyUnlessAskedOtherwise() {
		scheduler.schedule(new RunnableTask(0, "chirper", () -> {}));
		scheduler.schedule(new RunnableTask(0, "chirper", () -> {}), true);

		assertEquals(List.of("run:false", "run:true"), scheduler.started);
	}

	@Test
	void startsEachKindOfTaskThroughItsOwnMethod() {
		scheduler.schedule(new RunnableTask(0, "chirper", () -> {}));
		scheduler.schedule(DelayedRunnableTask.builder().module("chirper").runnable(() -> {}).delay(1000).build());
		scheduler.schedule(PeriodicalRunnableTask.builder().module("chirper").runnable(() -> {}).delay(0).period(1000).build());

		assertEquals(List.of("run:false", "delayed:false", "periodical:false"), scheduler.started);
	}

	@Test
	void cancelStopsOnlyThatTask() {
		RunnableTask kept = new RunnableTask(0, "chirper", () -> {});
		RunnableTask cancelled = new RunnableTask(0, "chirper", () -> {});
		scheduler.schedule(kept);
		scheduler.schedule(cancelled);

		scheduler.cancel(cancelled);

		assertEquals(List.of(cancelled.getId()), scheduler.stopped);
	}

	@Test
	void cancelOfAnUnknownTaskStopsNothing() {
		scheduler.cancel(new RunnableTask(7, "bubbler", () -> {}));

		assertTrue(scheduler.stopped.isEmpty());
	}

	@Test
	void cancelByModuleStopsEveryTaskOfThatModuleOnce() {
		RunnableTask first = new RunnableTask(0, "chirper", () -> {});
		RunnableTask second = new RunnableTask(0, "chirper", () -> {});
		RunnableTask other = new RunnableTask(0, "bubbler", () -> {});
		scheduler.schedule(first);
		scheduler.schedule(second);
		scheduler.schedule(other);

		scheduler.cancelByModule("chirper");
		scheduler.cancelByModule("chirper");

		assertEquals(2, scheduler.stopped.size());
		assertTrue(scheduler.stopped.containsAll(List.of(first.getId(), second.getId())));
	}

	private static final class RecordingScheduler extends PlatformScheduler<Integer> {
		private final List<String> started = new ArrayList<>();
		private final List<Integer> stopped = new ArrayList<>();

		@Override
		@NotNull
		protected Integer start(@NotNull RunnableTask runnableTask, boolean async) {
			started.add("run:" + async);
			return runnableTask.getId();
		}

		@Override
		@NotNull
		protected Integer start(@NotNull DelayedRunnableTask runnableTask, boolean async) {
			started.add("delayed:" + async);
			return runnableTask.getId();
		}

		@Override
		@NotNull
		protected Integer start(@NotNull PeriodicalRunnableTask runnableTask, boolean async) {
			started.add("periodical:" + async);
			return runnableTask.getId();
		}

		@Override
		protected void stop(@NotNull Integer task) {
			stopped.add(task);
		}
	}
}
