package me.whereareiam.socialismus.common.updater;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.AnsiColor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.lifecycle.StartupPhase;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.config.Updater;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.model.update.UpdateConfiguration;
import me.whereareiam.socialismus.model.update.UpdateSource;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.UpdateProvider;
import me.whereareiam.socialismus.type.module.ProviderType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Checks for new versions of Socialismus at the interval the updater document names, from the moment Socialismus handles
 * players. After a reload it schedules the checks again with the reloaded updater document.
 */
@Singleton
public final class UpdateScheduler implements RuntimeLifecycle, EventListener {
	private static final int BRANCH_UPDATE_LIMIT = 50;
	private static final String UPDATER_MODULE = "main";

	/**
	 * Core plugin: release via Modrinth, dev via GitHub
	 */
	private static final UpdateConfiguration CORE_SPEC = UpdateConfiguration.builder()
			.release(UpdateSource.builder()
					.provider(ProviderType.MODRINTH)
					.id("socialismus")
					.build())
			.dev(UpdateSource.builder()
					.provider(ProviderType.GITHUB)
					.id("whereareiam/Socialismus")
					.build())
			.build();

	private final Provider<Updater> updater;
	private final Scheduler scheduler;
	private final UpdateProviderRegistry providers;
	private final EventManager eventManager;

	@Inject
	public UpdateScheduler(
			Provider<Updater> updater,
			Scheduler scheduler,
			UpdateProviderRegistry providers,
			EventManager eventManager
	) {
		this.updater = updater;
		this.scheduler = scheduler;
		this.providers = providers;
		this.eventManager = eventManager;
	}

	@Override
	public @NotNull StartupPhase phase() {
		return StartupPhase.CONNECTIONS;
	}

	@Override
	public void initialize() {
		schedule();
		eventManager.register(this);
	}

	@Override
	public void shutdown() {
		eventManager.unregister(this);
		scheduler.cancelByModule(UPDATER_MODULE);
	}

	@SocialisticEvent
	public void onReloaded(PluginReloadedEvent event) {
		scheduler.cancelByModule(UPDATER_MODULE);
		schedule();
	}

	private void schedule() {
		Updater cfg = updater.get();
		if (!cfg.isCheck() || cfg.getInterval() == null || cfg.getInterval().isZero() || cfg.getInterval().isNegative()) {
			Logger.debug("Update checking is disabled");
			return;
		}

		scheduler.schedule(
				PeriodicalRunnableTask.builder()
						.period(cfg.getInterval().toMillis())
						.runnable(this::runOnce)
						.module(UPDATER_MODULE)
						.delay(0)
						.build(),
				true
		);

		Logger.debug("Update scheduler started with interval: %s", cfg.getInterval());
	}

	private void runOnce() {
		checkEntry("Socialismus", Constants.VERSION, CORE_SPEC, updater.get());
	}

	private void checkEntry(
			String name,
			String current,
			UpdateConfiguration spec,
			Updater cfg
	) {
		// nothing configured?
		if (spec.getRelease() == null && spec.getDev() == null) return;

		// 1) LOCAL DEV exact build
		if ("DEV".equalsIgnoreCase(current)) {
			if (cfg.getWarn().isLocalBuilds()) {
				Logger.info(AnsiColor.RED +
						"You are running a local dev build of " + name + "." +
						AnsiColor.RESET);
			}
			return;
		}

		// 2) branch/CI build (anything not matching release‐pattern)
		if (!isReleaseVersion(current)) {
			if (cfg.getWarn().isDevBuilds() && spec.getDev() != null) {
				var devSpec = spec.getDev();
				UpdateProvider prov = providers.by(devSpec);
				warnAheadBehindBranches(current, name, prov, devSpec);
			}
			return;
		}

		// 3) release build
		if (cfg.getWarn().isUpdates() && spec.getRelease() != null) {
			var relSpec = spec.getRelease();
			UpdateProvider prov = providers.by(relSpec);
			try {
				prov.fetchLatest(relSpec).ifPresent(latest -> {
					if (!bothPureSemver(current, latest)) {
						Logger.debug(
								"Skipping semver compare for %s: current=\"%s\", latest=\"%s\"",
								name, current, latest
						);
						return;
					}

					if (compareSemver(latest, current) > 0) {
						Logger.warn(
								"The version of " + name + " you are using is outdated. " +
										"Current version: " + current + ", latest version: " + latest
						);
						return;
					}

					Logger.info("You are using the latest version of " + name + ".");
				});
			} catch (IOException ex) {
				Logger.debug("Failed to check for new releases for " + name + ". " + ex.getMessage());
			}
		}
	}

	private void warnAheadBehindBranches(
			String version,
			String name,
			UpdateProvider provider,
			UpdateSource source
	) {
		String prefix = version.substring(version.lastIndexOf('-') + 1)
				.toLowerCase(Locale.ROOT);

		try {
			List<String> updates = provider.fetchRecentUpdates(source, BRANCH_UPDATE_LIMIT);
			int behind = 0;
			for (String id : updates) {
				if (id.toLowerCase(Locale.ROOT).startsWith(prefix)) break;
				behind++;
			}
			if (behind == 0) {
				Logger.info("You are using the latest dev build of " + name + ".");
				return;
			}

			Logger.warn(
					"You are " + behind + " commit" + (behind == 1 ? "" : "s") + " behind the latest dev build of " + name + "."
			);
		} catch (IOException ex) {
			Logger.debug("Failed to check for new dev builds for " + name + ". " + ex.getMessage());
		}
	}

	/**
	 * Matches "1.2.3" or "1.2.3-RC1"
	 */
	private static boolean isReleaseVersion(String v) {
		return v.matches("\\d+\\.\\d+\\.\\d+(?:-[0-9A-Za-z.-]+)?");
	}

	/**
	 * true only if both sides are pure "x.y.z" (no suffix)
	 */
	private static boolean bothPureSemver(String a, String b) {
		return a.matches("\\d+\\.\\d+\\.\\d+") &&
				b.matches("\\d+\\.\\d+\\.\\d+");
	}

	/**
	 * Compares "x.y.z" numerically; caller ensures both match the pattern
	 */
	private static int compareSemver(String a, String b) {
		String[] xa = a.split("\\."), xb = b.split("\\.");
		for (int i = 0; i < 3; i++) {
			int diff = Integer.parseInt(xa[i]) - Integer.parseInt(xb[i]);
			if (diff != 0) return diff;
		}

		return 0;
	}
}
