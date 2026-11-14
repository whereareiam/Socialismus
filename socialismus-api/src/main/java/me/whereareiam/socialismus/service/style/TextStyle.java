package me.whereareiam.socialismus.service.style;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * A style of {@code styles.yml}, bound: it reads what a player wrote with only the tags it allows. Every other tag
 * stays text, as typed, and placeholders are never resolved.
 *
 * <pre>{@code
 * Component content = place.of(sender).read(text);
 * }</pre>
 */
public interface TextStyle {
	/**
	 * The style of a writer who gets none: every tag stays text.
	 */
	TextStyle PLAIN = new TextStyle() {
		@Override
		public @NotNull String id() {
			return "plain";
		}

		@Override
		public @NotNull Component read(@NotNull String text) {
			return Component.text(text);
		}
	};

	/**
	 * Returns the style's id, which travels with text read elsewhere, such as a private message on another server;
	 * {@code plain} for {@link #PLAIN}.
	 *
	 * @return the id
	 */
	@NotNull String id();

	/**
	 * Reads what a player wrote.
	 *
	 * @param text the text
	 * @return the text, formatted with the tags the style allows
	 */
	@NotNull Component read(@NotNull String text);
}
