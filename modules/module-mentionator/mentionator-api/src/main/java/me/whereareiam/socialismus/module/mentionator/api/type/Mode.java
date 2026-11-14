package me.whereareiam.socialismus.module.mentionator.api.type;

/**
 * What tags notify a player, as they choose with {@code /socialismus mentions}. Whatever the mode, a tag is shown
 * highlighted; a mode only silences notifications.
 */
public enum Mode {
	/**
	 * Every tag notifies the player.
	 */
	ALL,

	/**
	 * Only direct tags notify the player, such as their name; tags of groups such as {@code @everyone} do not.
	 */
	DIRECT,

	/**
	 * No tag notifies the player, except tags whose {@code bypass} the sender meets.
	 */
	NONE
}
