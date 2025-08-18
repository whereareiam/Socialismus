package me.whereareiam.socialismus.api.type.chat;

/**
 * Types of chat triggers supported by the routing system.
 */
public enum TriggerType {
	/**
	 * Literal symbol prefix like "!", "#" at the start of the message
	 */
	SYMBOL,
	/**
	 * Regular expression matched against the message plain text
	 */
	REGEX,
	/**
	 * Dedicated command (alias) to send the message into a chat
	 */
	COMMAND
}