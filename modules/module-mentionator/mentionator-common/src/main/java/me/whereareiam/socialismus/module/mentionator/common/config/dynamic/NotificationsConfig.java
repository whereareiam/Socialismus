package me.whereareiam.socialismus.module.mentionator.common.config.dynamic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 * A file of {@code notifications/}.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsConfig {
	private List<Notification> notifications = new ArrayList<>();
}
