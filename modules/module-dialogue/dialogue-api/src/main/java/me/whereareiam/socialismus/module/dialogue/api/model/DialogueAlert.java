package me.whereareiam.socialismus.module.dialogue.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * What draws a viewer's attention to a private message besides the message itself, played with the format it
 * belongs to. Each part is optional. The texts take the same placeholders as the format: {@code {senderName}},
 * {@code {recipientName}} and {@code {message}}, which is shown as written.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DialogueAlert {
	/**
	 * A sound to play, by its key, such as {@code minecraft:entity.experience_orb.pickup}; none when absent.
	 */
	private @Nullable String sound;

	/**
	 * A text shown above the hotbar; none when absent.
	 */
	private @Nullable String actionBar;

	/**
	 * A title shown in the middle of the screen; none when absent.
	 */
	private @Nullable Title title;

	/**
	 * A title and its subtitle, and how long they fade in, stay and fade out.
	 */
	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class Title {
		private @Nullable String title;
		private @Nullable String subtitle;
		private Duration fadeIn = Duration.ofMillis(250);
		private Duration stay = Duration.ofSeconds(2);
		private Duration fadeOut = Duration.ofMillis(500);
	}
}
