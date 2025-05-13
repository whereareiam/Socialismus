package me.whereareiam.socialismus.api.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.api.type.chat.Participants;

import java.util.List;
import java.util.Map;

/**
 * Represents a chat configuration with its parameters, formats, and requirements.
 * This class is used to define chat settings and behaviors in the Socialismus system.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Chat {
	/**
	 * Unique identifier for the chat
	 */
	private String id;

	/**
	 * Priority level of the chat
	 */
	private int priority;

	/**
	 * Flag indicating if the chat is enabled
	 */
	private boolean enabled;

	/**
	 * Parameters controlling chat behavior
	 */
	private ChatParameters parameters;

	/**
	 * List of formatting rules for the chat
	 */
	private List<ChatFormat> formats;

	/**
	 * Map of requirements for different participant types
	 */
	private Map<Participants, RequirementGroup> requirements;
}