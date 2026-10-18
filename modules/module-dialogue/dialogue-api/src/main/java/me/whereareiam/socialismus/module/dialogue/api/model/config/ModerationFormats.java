package me.whereareiam.socialismus.module.dialogue.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;

import java.util.List;

/**
 * How staff see private messages, {@code formats/moderation} in Dialogue's working directory. A spy sees the last
 * format of the list whose requirements the sender and the recipient meet.
 */
@Getter
@Setter
@ToString
public class ModerationFormats {
	/**
	 * What a spy sees of a private message between two other players.
	 */
	private List<DialogueFormat> spy;
}
