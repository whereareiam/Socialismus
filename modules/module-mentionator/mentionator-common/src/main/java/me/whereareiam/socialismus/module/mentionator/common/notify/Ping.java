package me.whereareiam.socialismus.module.mentionator.common.notify;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * One tag that tags a player, as the server of the tagged player needs it to notify them.
 *
 * @param tag      the tag's id
 * @param match    what the tag matched, for {@code {match}}
 * @param notifications the ids of its notifications
 * @param direct   whether it names the player, so it notifies players who hear only direct tags
 * @param bypasses whether the sender meets its {@code bypass}, so it notifies players who turned tags off
 */
public record Ping(@NotNull String tag, @NotNull String match, @NotNull List<String> notifications, boolean direct, boolean bypasses) {
	public Ping {
		notifications = List.copyOf(notifications);
	}
}
