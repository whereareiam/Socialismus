package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.platform.common.PlatformScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Runs tasks on Paper's asynchronous scheduler, which also works on Folia; every task runs off the main thread.
 */
@Singleton
public class PaperScheduler extends PlatformScheduler<ScheduledTask> {
	private final Plugin plugin;

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

	@Override
	@NotNull
	protected ScheduledTask start(@NotNull RunnableTask runnableTask, boolean async) {
		return Bukkit.getAsyncScheduler()
				.runNow(plugin, t -> runnableTask.getRunnable().run());
	}

	@Override
	@NotNull
	protected ScheduledTask start(@NotNull DelayedRunnableTask runnableTask, boolean async) {
		return Bukkit.getAsyncScheduler()
				.runDelayed(plugin,
						t -> runnableTask.getRunnable().run(),
						runnableTask.getDelay(),
						TimeUnit.MILLISECONDS);
	}

	@Override
	@NotNull
	protected ScheduledTask start(@NotNull PeriodicalRunnableTask runnableTask, boolean async) {
		return Bukkit.getAsyncScheduler()
				.runAtFixedRate(plugin,
						t -> runnableTask.getRunnable().run(),
						runnableTask.getDelay(),
						runnableTask.getPeriod(),
						TimeUnit.MILLISECONDS);
	}

	@Override
	protected void stop(@NotNull ScheduledTask task) {
		task.cancel();
	}
}
