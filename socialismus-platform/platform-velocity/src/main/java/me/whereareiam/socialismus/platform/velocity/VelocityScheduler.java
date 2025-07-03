package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import me.whereareiam.socialismus.api.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.api.model.scheduler.RunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class VelocityScheduler implements Scheduler {
	private final VelocitySocialismus socialismus;
	private final com.velocitypowered.api.scheduler.Scheduler velocityScheduler;
	private final Map<String, Map<Integer, ScheduledTask>> tasks = new ConcurrentHashMap<>();

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);

	@Inject
	public VelocityScheduler(
			VelocitySocialismus socialismus,
			ProxyServer proxyServer
	) {
		this.socialismus = socialismus;
		this.velocityScheduler = proxyServer.getScheduler();
	}

	private int ensureId(RunnableTask task) {
		int existing = task.getId();
		if (existing <= 0) {
			int id = ID_GENERATOR.getAndIncrement();
			task.setId(id);
			return id;
		}

		return existing;
	}

	@Override
	public void schedule(RunnableTask runnableTask) {
		int id = ensureId(runnableTask);

		ScheduledTask task = velocityScheduler
				.buildTask(socialismus, runnableTask.getRunnable())
				.schedule();

		tasks
				.computeIfAbsent(runnableTask.getModule(), m -> new ConcurrentHashMap<>())
				.put(id, task);
	}

	@Override
	public void schedule(DelayedRunnableTask runnableTask) {
		int id = ensureId(runnableTask);

		ScheduledTask task = velocityScheduler
				.buildTask(socialismus, runnableTask.getRunnable())
				.delay(Duration.ofMillis(runnableTask.getDelay()))
				.schedule();

		tasks
				.computeIfAbsent(runnableTask.getModule(), m -> new ConcurrentHashMap<>())
				.put(id, task);
	}

	@Override
	public void schedule(PeriodicalRunnableTask runnableTask) {
		int id = ensureId(runnableTask);

		ScheduledTask task = velocityScheduler
				.buildTask(socialismus, runnableTask.getRunnable())
				.delay(Duration.ofMillis(runnableTask.getDelay()))
				.repeat(Duration.ofMillis(runnableTask.getPeriod()))
				.schedule();

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
		if (moduleTasks != null)
			moduleTasks.values().forEach(ScheduledTask::cancel);
	}
}
