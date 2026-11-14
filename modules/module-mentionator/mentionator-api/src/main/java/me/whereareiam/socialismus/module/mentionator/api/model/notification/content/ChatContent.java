package me.whereareiam.socialismus.module.mentionator.api.model.notification.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.NotificationContent;
import org.jetbrains.annotations.Nullable;

/**
 * A line in the tagged player's chat.
 */
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ChatContent extends NotificationContent {
	/**
	 * The line.
	 */
	private String chat;

	/**
	 * What the tagged player must meet to get this content, checked with the parts target and sender; everyone when
	 * absent.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private @Nullable Requirements requires;
}
