package me.whereareiam.socialismus.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.config.Updater;

import java.time.Duration;

@Singleton
public class UpdaterDefaults implements DefaultsProvider<Updater> {
	@Override
	public Updater supply(Updater updater) {
		updater.setCheck(true);
		updater.setInterval(Duration.ofHours(1));

		Updater.Warn warn = new Updater.Warn();
		warn.setUpdates(true);
		warn.setLocalBuilds(true);
		warn.setDevBuilds(true);
		updater.setWarn(warn);

		return updater;
	}
}
