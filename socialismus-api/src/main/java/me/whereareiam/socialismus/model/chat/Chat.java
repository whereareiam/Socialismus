package me.whereareiam.socialismus.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;

import java.util.List;

/**
 * Represents a chat configuration with its parameters, formats, and conditions.
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
	 * Triggers that route messages into this chat and may carry per-trigger radius.
	 */
	private List<ChatTrigger> triggers;

	/**
	 * List of formatting rules for the chat
	 */
	private List<ChatFormat> formats;

	/**
	 * What the sender must meet to write in this chat, checked with the parts sender and message.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements requires = Requirements.none();

	/**
	 * What a player must meet to receive the chat's messages, checked with the parts recipient, sender and message.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements reaches = Requirements.none();

	/**
	 * Whether players may turn this chat off for themselves; a chat that must always reach everyone, such as staff
	 * announcements, sets it to {@code false}.
	 */
	@Builder.Default
	private boolean toggleable = true;
}