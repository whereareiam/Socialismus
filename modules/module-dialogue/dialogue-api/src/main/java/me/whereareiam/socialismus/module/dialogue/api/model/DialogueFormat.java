package me.whereareiam.socialismus.module.dialogue.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.type.chat.Participants;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * One way a private message is shown, as a chat format is: of a list of formats, a viewer sees the last one whose
 * requirements are met.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DialogueFormat {
	/**
	 * The text shown, with {@code {senderName}}, {@code {recipientName}} and {@code {message}} filled in.
	 */
	private String format;

	/**
	 * What the sender ({@link Participants#SENDER}) and the recipient ({@link Participants#RECIPIENT}) must meet
	 * for this format; a participant without a group meets it.
	 */
	private Map<Participants, RequirementGroup> requirements;

	/**
	 * What else the viewer of this format gets, such as a sound; nothing when absent.
	 */
	private @Nullable DialogueAlert alert;

	/**
	 * Creates a format without an alert.
	 *
	 * @param format       the text shown
	 * @param requirements what the participants must meet for it
	 */
	public DialogueFormat(String format, Map<Participants, RequirementGroup> requirements) {
		this(format, requirements, null);
	}
}
