package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.platform.common.PlatformScheduler;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * Runs tasks on Velocity's scheduler, which runs every task off the main thread.
 */
@Singleton
public class VelocityScheduler extends PlatformScheduler<ScheduledTask> {
	private final VelocitySocialismus socialismus;
	private final com.velocitypowered.api.scheduler.Scheduler velocityScheduler;

	@Inject
	public VelocityScheduler(
			VelocitySocialismus socialismus,
			ProxyServer proxyServer
	) {
		this.socialismus = socialismus;
		this.velocityScheduler = proxyServer.getScheduler();
	}

	@Override
	@NotNull
	protected ScheduledTask start(@NotNull RunnableTask runnableTask, boolean async) {
		return velocityScheduler
				.buildTask(socialismus, runnableTask.getRunnable())
				.schedule();
	}

	@Override
	@NotNull
	protected ScheduledTask start(@NotNull DelayedRunnableTask runnableTask, boolean async) {
		return velocityScheduler
				.buildTask(socialismus, runnableTask.getRunnable())
				.delay(Duration.ofMillis(runnableTask.getDelay()))
				.schedule();
	}

	@Override
	@NotNull
	protected ScheduledTask start(@NotNull PeriodicalRunnableTask runnableTask, boolean async) {
		return velocityScheduler
				.buildTask(socialismus, runnableTask.getRunnable())
				.delay(Duration.ofMillis(runnableTask.getDelay()))
				.repeat(Duration.ofMillis(runnableTask.getPeriod()))
				.schedule();
	}

	@Override
	protected void stop(@NotNull ScheduledTask task) {
		task.cancel();
	}
}
