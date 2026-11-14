package me.whereareiam.socialismus.module.mentionator.api.model.notification;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.feature.polymorphic.api.annotation.Polymorphic;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.ActionbarContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.BossbarContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.ChatContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.SoundContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.TitleContent;
import org.jetbrains.annotations.Nullable;

/**
 * One content of a notification, known by the field that holds what it shows: {@code chat}, {@code actionbar},
 * {@code title}, {@code sound} or {@code bossbar}. Its texts fill {@code {sender}}, {@code {tag}}, {@code {match}},
 * {@code {message}}, {@code {chat}} and {@code {server}}, and the tagged player's PlaceholderAPI placeholders.
 */
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Polymorphic(
		inferBy = {
				@Polymorphic.Infer(field = "chat", target = ChatContent.class),
				@Polymorphic.Infer(field = "actionbar", target = ActionbarContent.class),
				@Polymorphic.Infer(field = "title", target = TitleContent.class),
				@Polymorphic.Infer(field = "sound", target = SoundContent.class),
				@Polymorphic.Infer(field = "bossbar", target = BossbarContent.class)
		},
		defaultTarget = ChatContent.class
)
public class NotificationContent {
	/**
	 * Returns what the tagged player must meet to get this content, checked with the parts target and sender. Each
	 * kind of content holds it as its last field, so it is written last.
	 *
	 * @return the list, {@code null} for everyone
	 */
	public @Nullable Requirements getRequires() {
		return null;
	}
}
