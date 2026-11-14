package me.whereareiam.socialismus.module.mentionator.common.notify;

/**
 * The notifications a player got in their current window of {@code limits.received}.
 *
 * @param count how many
 * @param until when the window ends, in milliseconds since the epoch
 */
public record ReceivedWindow(int count, long until) {
}
