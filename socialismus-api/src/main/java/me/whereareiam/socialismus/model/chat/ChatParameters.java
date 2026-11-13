package me.whereareiam.socialismus.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;

import java.util.List;

/**
 * What players may do in a chat, its {@code parameters}:
 *
 * <pre>{@code
 * parameters:
 *   toggleable: true
 *   styles: ["basic", "full"]
 * }</pre>
 */
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatParameters {
	/**
	 * Whether players may turn the chat off for themselves; a chat that must always reach everyone, such as staff
	 * announcements, sets it to {@code false}.
	 */
	@Builder.Default
	private boolean toggleable = true;

	/**
	 * The styles of {@code styles.yml} whose tags a sender may use in the chat, by id, from lowest to highest: the
	 * sender gets the last one whose requirements they meet. Without one, what a sender writes stays plain text.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private List<String> styles = List.of();
}
