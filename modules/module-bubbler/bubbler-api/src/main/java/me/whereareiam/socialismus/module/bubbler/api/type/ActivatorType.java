package me.whereareiam.socialismus.module.bubbler.api.type;

/**
 * Defines the mechanism that activated a bubble.
 * Used to distinguish between bubbles triggered by chat messages vs direct commands.
 */
public enum ActivatorType {
	/**
	 * Bubble was activated through Socialismus chat system
	 */
	CHAT,
	/**
	 * Bubble was activated through Bubbler's command
	 */
	COMMAND
}