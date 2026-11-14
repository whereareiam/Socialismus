package me.whereareiam.socialismus.testing.mentionator;

import me.whereareiam.socialismus.testing.environment.ServerFiles;

import java.util.List;
import java.util.Map;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;

/**
 * The documents of Mentionator's working directory that its journeys write, and the chat they tag in.
 */
final class MentionatorFiles {
	static final String SETTINGS = "modules/mentionator/settings.yml";
	static final String NOTIFICATIONS = "modules/mentionator/notifications/journey.yml";

	private MentionatorFiles() {
	}

	/**
	 * One global chat for every message, in a format the journeys read.
	 */
	static void globalChat(ServerFiles files) {
		files.chats(chat("global", 0, "", "[G] {playerName}: {message}"));
	}

	/**
	 * Notifications the default tags use, as chat lines a journey can also expect never to arrive: {@code ping} for a
	 * player's name, {@code everyone} for {@code @everyone}.
	 */
	static void chatNotifications(ServerFiles files) {
		files.put(NOTIFICATIONS, Map.of("notifications", List.of(
				notification("ping", "PING from {sender}"),
				notification("everyone", "EVERYONE from {sender}")
		)));
	}

	/**
	 * Settings without the limits of how often a player is notified, so a journey tags the same player again at once.
	 */
	static void withoutRepeatLimits(ServerFiles files) {
		files.put(SETTINGS, Map.of("limits", Map.of("repeat", "0s", "received", Map.of("count", -1))));
	}

	private static Map<String, Object> notification(String id, String line) {
		return Map.of("id", id, "contents", List.of(Map.of("chat", line)));
	}
}
