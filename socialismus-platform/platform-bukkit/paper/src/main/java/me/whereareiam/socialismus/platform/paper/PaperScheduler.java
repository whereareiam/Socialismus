package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class PaperScheduler implements Scheduler {
	private final Plugin plugin;
	private final Map<String, Map<Integer, ScheduledTask>> tasks = new ConcurrentHashMap<>();

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

	@Inject
	public PaperScheduler(Plugin plugin) {
		this.plugin = plugin;
		checkAsyncSchedulerAvailability();
	}

	private void checkAsyncSchedulerAvailability() {
		try {
			Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException(
					"AsyncScheduler not found. Please use Paper version 1.20 or newer.", e);
		}
	}

	private int ensureId(RunnableTask task) {
		int existing = task.getId();
		if (existing <= 0) {
			int generated = ID_GENERATOR.getAndIncrement();
			task.setId(generated);

			return generated;
		}
		return existing;
	}

	@Override
	public void schedule(RunnableTask runnableTask) {
		int id = ensureId(runnableTask);
		ScheduledTask task = Bukkit.getAsyncScheduler()
				.runNow(plugin, t -> runnableTask.getRunnable().run());

		tasks
				.computeIfAbsent(runnableTask.getModule(), m -> new ConcurrentHashMap<>())
				.put(id, task);
	}

	@Override
	public void schedule(DelayedRunnableTask runnableTask) {
		int id = ensureId(runnableTask);
		ScheduledTask task = Bukkit.getAsyncScheduler()
				.runDelayed(plugin,
						t -> runnableTask.getRunnable().run(),
						runnableTask.getDelay(),
						TimeUnit.MILLISECONDS);

		tasks
				.computeIfAbsent(runnableTask.getModule(), m -> new ConcurrentHashMap<>())
				.put(id, task);
	}

	@Override
	public void schedule(PeriodicalRunnableTask runnableTask) {
		int id = ensureId(runnableTask);
		ScheduledTask task = Bukkit.getAsyncScheduler()
				.runAtFixedRate(plugin,
						t -> runnableTask.getRunnable().run(),
						runnableTask.getDelay(),
						runnableTask.getPeriod(),
						TimeUnit.MILLISECONDS);

		tasks
				.computeIfAbsent(runnableTask.getModule(), m -> new ConcurrentHashMap<>())
				.put(id, task);
	}

	@Override
	public void schedule(RunnableTask runnableTask, boolean async) {
		schedule(runnableTask);
	}

	@Override
	public void schedule(DelayedRunnableTask runnableTask, boolean async) {
		schedule(runnableTask);
	}

	@Override
	public void schedule(PeriodicalRunnableTask runnableTask, boolean async) {
		schedule(runnableTask);
	}

	@Override
	public void cancel(RunnableTask runnableTask) {
		Map<Integer, ScheduledTask> moduleTasks = tasks.get(runnableTask.getModule());
		if (moduleTasks != null) {
			Integer id = runnableTask.getId();
			ScheduledTask task = moduleTasks.remove(id);
			if (task != null)
				task.cancel();
			if (moduleTasks.isEmpty())
				tasks.remove(runnableTask.getModule());
		}
	}

	@Override
	public void cancel(DelayedRunnableTask runnableTask) {
		cancel((RunnableTask) runnableTask);
	}

	@Override
	public void cancelByModule(String module) {
		Map<Integer, ScheduledTask> moduleTasks = tasks.remove(module);
		if (moduleTasks != null) {
			moduleTasks.values().forEach(ScheduledTask::cancel);
		}
	}
}
