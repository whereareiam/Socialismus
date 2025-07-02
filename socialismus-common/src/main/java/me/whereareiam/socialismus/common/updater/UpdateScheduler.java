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

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UpdateScheduler {
	private static final long MS_PER_HOUR = 3_600_000L;
	private static final int BRANCH_UPDATE_LIMIT = 50;

	/**
	 * “Core” update spec for the main plugin
	 */
	private static final UpdateSpecification CORE_SPEC =
			UpdateSpecification.builder()
					.provider(ProviderType.MODRINTH)
					.id("socialismus")
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

		checkEntry("Socialismus", Constants.VERSION, CORE_SPEC, cfg);

		moduleService.getModules().stream()
				.filter(m -> m.getUpdater() != null)
				.forEach(m -> checkEntry(
						m.getName(),
						m.getVersion(),
						m.getUpdater(),
						cfg
				));
	}

	/**
	 * Universal check for either the core plugin or a loaded module.
	 */
	private void checkEntry(
			String name,
			String current,
			UpdateSpecification spec,
			Settings.Updater cfg
	) {
		UpdateProvider provider = providers.by(spec);

		// 1) local-dev build?
		if ("DEV".equals(current)) {
			if (cfg.isWarnAboutLocalBuilds()) {
				Logger.info(
						AnsiColor.RED +
								"You are running a local dev build of " +
								name + "." +
								AnsiColor.RESET
				);
			}
			return;
		}

		// 2) branch build?
		if (current.startsWith("BRANCH-")) {
			if (cfg.isWarnAboutDevBuilds()) {
				warnAheadBehindBranches(
						current, name, provider, spec
				);
			}
			return;
		}

		// 3) release build?
		if (!cfg.isWarnAboutUpdates()) return;

		try {
			provider.fetchLatest(spec).ifPresent(latest -> {
				if (!isSemver(current) || !isSemver(latest)) {
					Logger.debug(
							"Skipping semver compare for %s: current=\"%s\", latest=\"%s\"",
							name, current, latest
					);
					return;
				}
				if (compareSemver(latest, current) > 0) {
					Logger.warn(
							"%s is outdated. Current %s, latest %s",
							name, current, latest
					);
				} else {
					Logger.info("%s is up to date.", name);
				}
			});
		} catch (IOException e) {
			Logger.debug(
					"Update check failed for %s: %s",
					name, e.getMessage()
			);
		}
	}

	private void warnAheadBehindBranches(
			String current,
			String name,
			UpdateProvider p,
			UpdateSpecification spec
	) {
		String prefix = current.substring("BRANCH-".length()).toLowerCase();
		try {
			List<String> updates = p.fetchRecentUpdates(spec, BRANCH_UPDATE_LIMIT);
			int behind = 0;
			for (String id : updates) {
				if (id.toLowerCase().startsWith(prefix)) break;
				behind++;
			}
			if (behind == 0) {
				Logger.info("%s is on the latest branch build.", name);
			} else {
				Logger.warn(
						"%s is behind the branch build by %d update%s.",
						name, behind, (behind == 1 ? "" : "s")
				);
			}
		} catch (IOException ex) {
			Logger.debug(
					"Branch check failed for %s: %s",
					name, ex.getMessage()
			);
		}
	}

	/**
	 * true iff v matches “x.y.z”
	 */
	private static boolean isSemver(String v) {
		return v.matches("\\d+\\.\\d+\\.\\d+");
	}

	/**
	 * Compare two 3-part semver strings.  Caller ensures both match isSemver().
	 *
	 * @return >0 if a>b, <0 if a<b, 0 if equal.
	 */
	private static int compareSemver(String a, String b) {
		String[] xa = a.split("\\."), xb = b.split("\\.");
		for (int i = 0; i < 3; i++) {
			int d = Integer.parseInt(xa[i]) - Integer.parseInt(xb[i]);
			if (d != 0) return d;
		}

		return 0;
	}
}
