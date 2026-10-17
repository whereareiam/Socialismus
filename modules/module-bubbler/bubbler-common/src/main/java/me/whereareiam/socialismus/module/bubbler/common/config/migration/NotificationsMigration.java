package me.whereareiam.socialismus.module.bubbler.common.config.migration;

import me.whereareiam.strata.MigrationAction;
import me.whereareiam.strata.adapter.configura.ConfigContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Renames Bubbler's {@code notify} section to {@code notifications} and drops the {@code notify} from each of its
 * switches, as Socialismus's chat settings name them: {@code notify.notifyNoPlayers} becomes
 * {@code notifications.noPlayers}.
 */
final class NotificationsMigration implements MigrationAction<ConfigContext> {
	static final int VERSION = 3;
	static final String NAME = "notifications";

	private final String settings;

	NotificationsMigration(@NotNull String extension) {
		this.settings = "settings" + extension;
	}

	@Override
	public void apply(@NotNull ConfigContext files) {
		for (String notification : List.of("NoPlayers", "NoNearbyPlayers", "NoBubbleSelected")) {
			String key = Character.toLowerCase(notification.charAt(0)) + notification.substring(1);
			files.move(settings, "/notify/notify" + notification, settings, "/notifications/" + key);
		}
		files.removeIfEmpty(settings, "/notify");
	}
}
