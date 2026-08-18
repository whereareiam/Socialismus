package me.whereareiam.socialismus.module.chirper.common.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.service.Scheduler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BossBarUtil {
	private final Scheduler scheduler;

	public void createTemporaryBossBar(Actor actor, BossBar bossBar, int duration) {
		Audience audience = actor.getAudience();
		audience.showBossBar(bossBar);

		double damagePerSecond = 1.0 / duration;

		for (int i = 1; i <= duration; i++) {
			DelayedRunnableTask task = DelayedRunnableTask.builder()
					.module("chirper")
					.delay(i * 1000L)
					.runnable(() -> {
						if (bossBar.progress() > 0) {
							bossBar.progress(Math.max(0, bossBar.progress() - (float) damagePerSecond));
						}
					})
					.build();

			scheduler.schedule(task);
		}

		DelayedRunnableTask hideTask = DelayedRunnableTask.builder()
				.module("chirper")
				.delay(duration * 1000L)
				.runnable(() -> audience.hideBossBar(bossBar))
				.build();

		scheduler.schedule(hideTask);
	}
}
