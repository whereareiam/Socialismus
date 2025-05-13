package me.whereareiam.socialismus.api.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.socialismus.api.type.chat.ChatType;

/**
 * Represents the configuration parameters for a chat channel.
 * This class defines the basic characteristics of how a chat channel
 * should behave and be identified.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatParameters {
	/**
	 * The type of chat channel (e.g., GLOBAL, LOCAL, CUSTOM).
	 */
	private ChatType type;

	/**
	 * The symbol or prefix that identifies this chat channel.
	 */
	private String symbol;

	/**
	 * The radius (in blocks) within which players can receive messages.
	 * Only applicable for LOCAL chat types.
	 */
	private int radius;
}