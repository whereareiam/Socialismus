package me.whereareiam.socialismus.module.mentionator.api.model.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * What a tagged player gets, named so tags refer to it with {@code notify}: a list of contents, such as a sound and a
 * title. A notification file in {@code notifications/} holds a list of them.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
	/**
	 * The name tags use in {@code notify}, such as {@code ping}.
	 */
	private String id;

	/**
	 * What the player gets, in order.
	 */
	private List<NotificationContent> contents = new ArrayList<>();
}
