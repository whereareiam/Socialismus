package me.whereareiam.socialismus.module.dialogue.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;
import org.jetbrains.annotations.Nullable;


/**
 * One way a private message is shown, as a chat format is: of a list of formats, a viewer sees the last one whose
 * conditions are met.
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
	 * What the sender must meet for this format, checked with the part sender.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements requires = Requirements.none();

	/**
	 * What the recipient must meet for this format, checked with the parts recipient and sender. Where the recipient
	 * is on another server, only an empty list is met.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements reaches = Requirements.none();

	/**
	 * What else the viewer of this format gets, such as a sound; nothing when absent.
	 */
	private @Nullable DialogueAlert alert;

	/**
	 * Creates a format without an alert.
	 *
	 * @param format   the text shown
	 * @param requires what the sender must meet for it
	 * @param reaches  what the recipient must meet for it
	 */
	public DialogueFormat(String format, Requirements requires, Requirements reaches) {
		this(format, requires, reaches, null);
	}
}
