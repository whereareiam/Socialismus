package me.whereareiam.socialismus.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;

/**
 * Represents the formatting configuration for a chat message.
 * This class defines how messages should be formatted and what conditions
 * different participants need to meet to use this format.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatFormat {
	/**
	 * The format string used for message formatting.
	 * Can include placeholders and formatting codes.
	 */
	private String format;

	/**
	 * What the sender must meet for this format, checked with the parts sender and message. The last format of a chat
	 * whose conditions the sender meets is used.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements requires = Requirements.none();
}