package me.whereareiam.socialismus.module.mentionator.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.Notification;
import me.whereareiam.socialismus.module.mentionator.common.config.defaults.NotificationsDefaults;
import me.whereareiam.socialismus.module.mentionator.common.config.dynamic.NotificationsConfig;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.type.PlatformType;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * The notifications of every file in {@code notifications/}; an empty folder gets {@code pings} and {@code alerts}.
 */
@Singleton
public class NotificationsProvider extends ConfigProvider<List<Notification>> {
	@Inject
	public NotificationsProvider(@Named("notificationsPath") Path notificationsPath, Registry<Reloadable> registry) {
		super(notificationsPath, "", listType(), registry);
	}

	@Override
	protected List<Notification> load() {
		return ConfigFolder.read(configura(), getPath(), NotificationsConfig.class, () -> NotificationsDefaults.files(PlatformType.isGameServer()))
				.stream()
				.flatMap(file -> file.getNotifications() == null ? Stream.empty() : file.getNotifications().stream())
				.toList();
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends List<Notification>> listType() {
		return (Class<? extends List<Notification>>) (Class<?>) List.class;
	}
}
