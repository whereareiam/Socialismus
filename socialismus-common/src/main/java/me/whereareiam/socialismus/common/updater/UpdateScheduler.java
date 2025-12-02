package me.whereareiam.socialismus.common.updater;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.AnsiColor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.input.updater.UpdateProvider;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.model.update.UpdateConfiguration;
import me.whereareiam.socialismus.model.update.UpdateSource;
import me.whereareiam.socialismus.output.Scheduler;
import me.whereareiam.socialismus.registry.Registry;
import me.whereareiam.socialismus.type.module.ProviderType;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Singleton
public final class UpdateScheduler implements Reloadable {
	private static final long MS_PER_HOUR = 3_600_000L;
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

	private final Provider<Settings> settings;
	private final Scheduler scheduler;
	private final UpdateProviderRegistry providers;

	@Inject
	public UpdateScheduler(
			Provider<Settings> settings,
			Scheduler scheduler,
			UpdateProviderRegistry providers,
			Registry<Reloadable> reloadableRegistry
	) {
		this.settings = settings;
		this.scheduler = scheduler;
		this.providers = providers;

		reloadableRegistry.register(this);
	}

	public void start() {
		var cfg = settings.get().getUpdater();
		if (!cfg.isCheckForUpdates() || cfg.getInterval() <= 0) {
			Logger.debug("Update checking is disabled");
			return;
		}

		scheduler.schedule(
				PeriodicalRunnableTask.builder()
						.period(cfg.getInterval() * MS_PER_HOUR)
						.runnable(this::runOnce)
						.module(UPDATER_MODULE)
						.delay(0)
						.build(),
				true
		);

		Logger.debug("Update scheduler started with interval: %d hours", cfg.getInterval());
	}

	private void runOnce() {
		checkEntry("Socialismus", Constants.VERSION, CORE_SPEC, settings.get().getUpdater());
	}

	private void checkEntry(
			String name,
			String current,
			UpdateConfiguration spec,
			Settings.Updater cfg
	) {
		// nothing configured?
		if (spec.getRelease() == null && spec.getDev() == null) return;

		// 1) LOCAL DEV exact build
		if ("DEV".equalsIgnoreCase(current)) {
			if (cfg.isWarnAboutLocalBuilds()) {
				Logger.info(AnsiColor.RED +
						"You are running a local dev build of " + name + "." +
						AnsiColor.RESET);
			}
			return;
		}

		// 2) branch/CI build (anything not matching release‐pattern)
		if (!isReleaseVersion(current)) {
			if (cfg.isWarnAboutDevBuilds() && spec.getDev() != null) {
				var devSpec = spec.getDev();
				UpdateProvider prov = providers.by(devSpec);
				warnAheadBehindBranches(current, name, prov, devSpec);
			}
			return;
		}

		// 3) release build
		if (cfg.isWarnAboutUpdates() && spec.getRelease() != null) {
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

	@Override
	public void reload() {
		scheduler.cancelByModule(UPDATER_MODULE);
		start();
	}
}
