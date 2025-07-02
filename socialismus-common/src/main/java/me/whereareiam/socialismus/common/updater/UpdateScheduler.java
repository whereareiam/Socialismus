package me.whereareiam.socialismus.common.updater;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.AnsiColor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.updater.UpdateProvider;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.model.module.UpdateSpecification;
import me.whereareiam.socialismus.api.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.api.output.module.ModuleService;
import me.whereareiam.socialismus.api.type.module.ProviderType;
import me.whereareiam.socialismus.shared.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class UpdateScheduler {
	private static final long MS_PER_HOUR = 3_600_000L;
	private static final int BRANCH_UPDATE_LIMIT = 50;

	/**
	 * Core plugin: release via Modrinth, dev via GitHub
	 */
	private static final UpdateSpecification CORE_SPEC = UpdateSpecification.builder()
			.release(UpdateSpecification.Spec.builder()
					.provider(ProviderType.MODRINTH)
					.id("socialismus")
					.build())
			.dev(UpdateSpecification.Spec.builder()
					.provider(ProviderType.GITHUB)
					.id("whereareiam/Socialismus")
					.build())
			.build();

	private final Provider<Settings> settings;
	private final Scheduler scheduler;
	private final ModuleService moduleService;
	private final UpdateProviderRegistry providers;

	public void start() {
		var cfg = settings.get().getUpdater();
		if (!cfg.isCheckForUpdates() || cfg.getInterval() <= 0) return;

		scheduler.schedule(
				PeriodicalRunnableTask.builder()
						.period(cfg.getInterval() * MS_PER_HOUR)
						.runnable(this::runOnce)
						.module("main")
						.delay(0)
						.build(),
				true
		);
	}

	private void runOnce() {
		var cfg = settings.get().getUpdater();

		// Core plugin
		checkEntry("Socialismus", Constants.VERSION, CORE_SPEC, cfg);

		// Every module that has an UpdateSpecification
		moduleService.getModules().stream()
				.map(m -> new Object[]{m.getName(), m.getVersion(), m.getUpdater()})
				.filter(t -> t[2] != null)
				.forEach(t -> checkEntry(
						(String) t[0],
						(String) t[1],
						(UpdateSpecification) t[2],
						cfg
				));
	}

	private void checkEntry(
			String name,
			String current,
			UpdateSpecification spec,
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
			UpdateSpecification.Spec spec
	) {
		String prefix = version.substring(version.lastIndexOf('-') + 1)
				.toLowerCase(Locale.ROOT);

		try {
			List<String> updates = provider.fetchRecentUpdates(spec, BRANCH_UPDATE_LIMIT);
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