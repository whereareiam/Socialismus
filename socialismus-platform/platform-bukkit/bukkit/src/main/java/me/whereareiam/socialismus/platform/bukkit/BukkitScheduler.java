package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.output.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class BukkitScheduler implements Scheduler {
	private final Plugin plugin;
	private final Map<String, Map<Integer, BukkitTask>> tasks = new ConcurrentHashMap<>();

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);
	private static final long MS_TO_TICKS = 20L;

	@Inject
	public BukkitScheduler(Plugin plugin) {
		this.plugin = plugin;
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
		BukkitTask task = async
				? Bukkit.getScheduler().runTaskAsynchronously(plugin, runnableTask.getRunnable())
				: Bukkit.getScheduler().runTask(plugin, runnableTask.getRunnable());

		tasks
				.computeIfAbsent(runnableTask.getModule(), m -> new ConcurrentHashMap<>())
				.put(id, task);
	}

	@Override
	public void schedule(DelayedRunnableTask runnableTask, boolean async) {
		int id = ensureId(runnableTask);
		long delayInTicks = runnableTask.getDelay() / MS_TO_TICKS;

		BukkitTask task = async
				? Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnableTask.getRunnable(), delayInTicks)
				: Bukkit.getScheduler().runTaskLater(plugin, runnableTask.getRunnable(), delayInTicks);

		tasks
				.computeIfAbsent(runnableTask.getModule(), m -> new ConcurrentHashMap<>())
				.put(id, task);
	}

	@Override
	public void schedule(PeriodicalRunnableTask runnableTask, boolean async) {
		int id = ensureId(runnableTask);
		long delayInTicks = runnableTask.getDelay() / MS_TO_TICKS;
		long periodInTicks = runnableTask.getPeriod() / MS_TO_TICKS;

		BukkitTask task = async
				? Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
				runnableTask.getRunnable(),
				delayInTicks,
				periodInTicks)
				: Bukkit.getScheduler().runTaskTimer(plugin,
				runnableTask.getRunnable(),
				delayInTicks,
				periodInTicks);

		tasks
				.computeIfAbsent(runnableTask.getModule(), m -> new ConcurrentHashMap<>())
				.put(id, task);
	}

	@Override
	public void cancel(RunnableTask runnableTask) {
		Map<Integer, BukkitTask> moduleTasks = tasks.get(runnableTask.getModule());
		if (moduleTasks != null) {
			Integer id = runnableTask.getId();
			BukkitTask t = moduleTasks.remove(id);
			if (t != null)
				t.cancel();
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
		Map<Integer, BukkitTask> moduleTasks = tasks.remove(module);
		if (moduleTasks != null) {
			moduleTasks.values().forEach(BukkitTask::cancel);
		}
	}
}
