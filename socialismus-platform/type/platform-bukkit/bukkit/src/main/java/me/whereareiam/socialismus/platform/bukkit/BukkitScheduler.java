package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.platform.common.PlatformScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Runs tasks on the Bukkit scheduler, on the main thread unless asked to run them asynchronously.
 */
@Singleton
public class BukkitScheduler extends PlatformScheduler<BukkitTask> {
	private static final long MILLIS_PER_TICK = 50L;

	private final Plugin plugin;

	@Inject
	public BukkitScheduler(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	@NotNull
	protected BukkitTask start(@NotNull RunnableTask runnableTask, boolean async) {
		return async
				? Bukkit.getScheduler().runTaskAsynchronously(plugin, runnableTask.getRunnable())
				: Bukkit.getScheduler().runTask(plugin, runnableTask.getRunnable());
	}

	@Override
	@NotNull
	protected BukkitTask start(@NotNull DelayedRunnableTask runnableTask, boolean async) {
		long delayInTicks = toTicks(runnableTask.getDelay());

		return async
				? Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnableTask.getRunnable(), delayInTicks)
				: Bukkit.getScheduler().runTaskLater(plugin, runnableTask.getRunnable(), delayInTicks);
	}

	@Override
	@NotNull
	protected BukkitTask start(@NotNull PeriodicalRunnableTask runnableTask, boolean async) {
		long delayInTicks = toTicks(runnableTask.getDelay());
		// A timer needs a period of at least one tick.
		long periodInTicks = Math.max(1L, toTicks(runnableTask.getPeriod()));

		return async
				? Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnableTask.getRunnable(), delayInTicks, periodInTicks)
				: Bukkit.getScheduler().runTaskTimer(plugin, runnableTask.getRunnable(), delayInTicks, periodInTicks);
	}

	/**
	 * Converts milliseconds to server ticks of 50 ms, rounding down; a delay under one tick runs on the next tick.
	 */
	static long toTicks(long millis) {
		return Math.max(0L, millis / MILLIS_PER_TICK);
	}

	@Override
	protected void stop(@NotNull BukkitTask task) {
		task.cancel();
	}
}
