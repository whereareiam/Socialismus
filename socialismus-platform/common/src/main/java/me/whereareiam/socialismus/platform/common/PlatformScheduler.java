package me.whereareiam.socialismus.platform.common;

import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.service.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Keeps the tasks Socialismus schedules, by module and id, so they can be cancelled one by one or per module; a
 * platform only starts and stops a task with its own scheduler.
 *
 * @param <T> the platform's handle of a started task
 */
public abstract class PlatformScheduler<T> implements Scheduler {
	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

	private final Map<String, Map<Integer, T>> tasks = new ConcurrentHashMap<>();

	@Override
	public void schedule(RunnableTask runnableTask) {
		schedule(runnableTask, false);
	}

	@Override
	public void schedule(DelayedRunnableTask runnableTask) {
		schedule(runnableTask, false);
	}

	@Override
	public void schedule(PeriodicalRunnableTask runnableTask) {
		schedule(runnableTask, false);
	}

	@Override
	public void schedule(RunnableTask runnableTask, boolean async) {
		int id = ensureId(runnableTask);
		track(runnableTask, id, start(runnableTask, async));
	}

	@Override
	public void schedule(DelayedRunnableTask runnableTask, boolean async) {
		int id = ensureId(runnableTask);
		track(runnableTask, id, start(runnableTask, async));
	}

	@Override
	public void schedule(PeriodicalRunnableTask runnableTask, boolean async) {
		int id = ensureId(runnableTask);
		track(runnableTask, id, start(runnableTask, async));
	}

	@Override
	public void cancel(RunnableTask runnableTask) {
		Map<Integer, T> moduleTasks = tasks.get(runnableTask.getModule());
		if (moduleTasks == null) return;

		T task = moduleTasks.remove(runnableTask.getId());
		if (task != null)
			stop(task);
		if (moduleTasks.isEmpty())
			tasks.remove(runnableTask.getModule());
	}

	@Override
	public void cancel(DelayedRunnableTask runnableTask) {
		cancel((RunnableTask) runnableTask);
	}

	@Override
	public void cancelByModule(String module) {
		Map<Integer, T> moduleTasks = tasks.remove(module);
		if (moduleTasks != null)
			moduleTasks.values().forEach(this::stop);
	}

	/**
	 * Runs a task once, right away.
	 *
	 * @param runnableTask the task to run
	 * @param async        whether the task should run off the main thread, where the platform has one
	 * @return the platform's handle of the started task
	 */
	@NotNull
	protected abstract T start(@NotNull RunnableTask runnableTask, boolean async);

	/**
	 * Runs a task once, after its delay.
	 *
	 * @param runnableTask the task to run
	 * @param async        whether the task should run off the main thread, where the platform has one
	 * @return the platform's handle of the started task
	 */
	@NotNull
	protected abstract T start(@NotNull DelayedRunnableTask runnableTask, boolean async);

	/**
	 * Runs a task after its delay and then repeatedly, once per period.
	 *
	 * @param runnableTask the task to run
	 * @param async        whether the task should run off the main thread, where the platform has one
	 * @return the platform's handle of the started task
	 */
	@NotNull
	protected abstract T start(@NotNull PeriodicalRunnableTask runnableTask, boolean async);

	/**
	 * Stops a started task.
	 *
	 * @param task the platform's handle of the task
	 */
	protected abstract void stop(@NotNull T task);

	private int ensureId(RunnableTask task) {
		int existing = task.getId();
		if (existing > 0) return existing;

		int id = ID_GENERATOR.getAndIncrement();
		task.setId(id);

		return id;
	}

	private void track(RunnableTask runnableTask, int id, T task) {
		tasks
				.computeIfAbsent(runnableTask.getModule(), module -> new ConcurrentHashMap<>())
				.put(id, task);
	}
}
