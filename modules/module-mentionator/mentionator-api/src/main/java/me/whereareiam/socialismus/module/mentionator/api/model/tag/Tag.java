package me.whereareiam.socialismus.module.mentionator.api.model.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;

import java.util.ArrayList;
import java.util.List;

/**
 * Something a player writes in chat to tag others, such as {@code @Steve}, {@code @everyone} or
 * {@code @%luckperms_primary_group_name%}: how it is written, how it looks, whom it notifies and who may use it. A tag
 * file in {@code tags/} holds a list of them.
 */
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
	/**
	 * The tag's name, such as {@code everyone}, for its permission, its phrases and {@code {tag}}.
	 */
	private String id;

	/**
	 * Whether the tag is used; a disabled tag is plain text.
	 */
	@Builder.Default
	private boolean enabled = true;

	/**
	 * Which tag a word is when it fits several: the higher priority wins.
	 */
	private int priority;

	/**
	 * The ways the tag is written. A form holding {@code {username}} tags the player of that name, one holding a
	 * {@code %placeholder%} tags every player whose value it is, and any other form tags every player the tag's
	 * {@code targets} let through, such as {@code @everyone}.
	 */
	@Builder.Default
	private List<String> written = new ArrayList<>();

	/**
	 * How the tag is matched and counted.
	 */
	@Builder.Default
	private TagParameters parameters = new TagParameters();

	/**
	 * How the tag looks in chat: of these, each viewer sees the last one whose {@code requires} the sender and whose
	 * {@code reaches} the viewer meets; the tag stays as written when none fits.
	 */
	@Builder.Default
	private List<TagFormat> formats = new ArrayList<>();

	/**
	 * The notifications a tagged player gets, by their id in {@code notifications/}.
	 */
	@Builder.Default
	private List<String> notify = new ArrayList<>();

	/**
	 * What the sender must meet to use the tag, checked with the parts sender and message; a tag the sender may not
	 * use stays plain text and the sender is told why.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements requires = Requirements.none();

	/**
	 * Which players the tag tags, checked for each player Socialismus knows with the parts target, sender and
	 * message.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements targets = Requirements.none();

	/**
	 * What the sender must meet for the tag to notify players who turned tags off, checked with the parts sender and
	 * message; nobody when absent.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements bypass;
}
