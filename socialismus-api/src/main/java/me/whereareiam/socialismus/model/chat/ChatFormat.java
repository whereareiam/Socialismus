package me.whereareiam.socialismus.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.type.chat.Participants;

import java.util.Map;

/**
 * Represents the formatting configuration for a chat message.
 * This class defines how messages should be formatted and what requirements
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
	 * Maps participant types to their respective requirement groups.
	 * Defines what requirements different participants need to meet
	 * to use this chat format.
	 */
	private Map<Participants, RequirementGroup> requirements;
}