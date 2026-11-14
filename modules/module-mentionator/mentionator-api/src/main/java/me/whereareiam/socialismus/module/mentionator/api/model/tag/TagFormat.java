package me.whereareiam.socialismus.module.mentionator.api.model.tag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;

/**
 * One way a tag looks in chat: of a tag's formats, each viewer sees the last one both lists let through.
 */
@Getter
@ToString
@NoArgsConstructor
public class TagFormat {
	/**
	 * The text shown in place of the tag, with {@code {match}} for what it matched in its own spelling, such as a
	 * player's name, and {@code {tag}} for the tag's id.
	 */
	private String format;

	/**
	 * What the sender must meet for this format, checked with the parts sender and message.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements requires = Requirements.none();

	/**
	 * What the viewer must meet for this format, checked with the parts recipient, sender and message; there
	 * {@code mentionator:targeted} holds for the players this tag tags.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements reaches = Requirements.none();

	/**
	 * Creates a format every viewer sees.
	 *
	 * @param format the text shown
	 */
	public TagFormat(String format) {
		this.format = format;
	}

	/**
	 * Creates a format for the senders and viewers two lists let through.
	 *
	 * @param format   the text shown
	 * @param requires what the sender must meet
	 * @param reaches  what the viewer must meet
	 */
	public TagFormat(String format, Requirements requires, Requirements reaches) {
		this.format = format;
		this.requires = requires;
		this.reaches = reaches;
	}

	/**
	 * Creates a format seen by the viewers a list lets through.
	 *
	 * @param format  the text shown
	 * @param reaches what the viewer must meet
	 */
	public TagFormat(String format, Requirements reaches) {
		this.format = format;
		this.reaches = reaches;
	}
}
