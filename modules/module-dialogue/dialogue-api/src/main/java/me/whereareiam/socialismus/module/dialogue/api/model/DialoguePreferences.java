package me.whereareiam.socialismus.module.dialogue.api.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.jetbrains.annotations.NotNull;

/**
 * What a player decided about private messages: whether they turned them off, and, for staff, whether they spy on
 * the messages of others. A player who decided nothing has {@link #NONE}.
 */
@Value
@With
@Builder(toBuilder = true)
public class DialoguePreferences {
	/**
	 * The preferences of a player who decided nothing: they take private messages and spy on nobody.
	 */
	public static final @NotNull DialoguePreferences NONE = DialoguePreferences.builder().build();

	/**
	 * Whether the player turned private messages off.
	 */
	boolean messagesOff;

	/**
	 * Whether the player sees the private messages of others.
	 */
	boolean spying;
}
