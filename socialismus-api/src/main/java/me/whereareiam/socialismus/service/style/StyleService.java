package me.whereareiam.socialismus.service.style;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Reads what players write with the styles of {@code styles.yml}. A place where players write, such as a chat, lists
 * the styles it accepts; the writer gets the last of them whose requirements they meet, and may use that style's
 * tags. Every other tag stays text, as typed, and placeholders are never resolved in what a player writes.
 *
 * <p>A module reads its players' text the same way:</p>
 *
 * <pre>{@code
 * Component content = styles.read(sender, text, bubble.getStyles());
 * }</pre>
 *
 * <p>When the text is shown somewhere the writer's requirements cannot be checked, such as on another server of a
 * network, the place choosing the style keeps its id with the text, and the other side reads it with
 * {@link #read(String, String)}:</p>
 *
 * <pre>{@code
 * String style = styles.styleOf(sender, settings.getStyles()).orElse(null); // where the player wrote
 * Component content = styles.read(text, style);                            // wherever it is shown
 * }</pre>
 */
public interface StyleService {
	/**
	 * Chooses the style of a writer: the last of the styles listed whose requirements they meet.
	 *
	 * @param writer the player who writes
	 * @param styles the ids of the styles the place accepts, in order; ids no style has are skipped
	 * @return the id of the style, empty when the writer meets none, or the place lists none
	 */
	@NotNull Optional<String> styleOf(@NotNull SocialismusPlayer writer, @NotNull List<String> styles);

	/**
	 * Reads a text with a style, without checking its requirements.
	 *
	 * @param text  what a player wrote
	 * @param style the id of the style; {@code null}, or an id no style has, reads the text as plain text
	 * @return the text, formatted with the tags the style allows
	 */
	@NotNull Component read(@NotNull String text, String style);

	/**
	 * Reads what a player wrote with the style they get among the ones a place accepts.
	 *
	 * @param writer the player who wrote the text
	 * @param text   the text
	 * @param styles the ids of the styles the place accepts, in order
	 * @return the text, formatted with the tags of the writer's style, or plain when they get none
	 */
	default @NotNull Component read(@NotNull SocialismusPlayer writer, @NotNull String text, @NotNull List<String> styles) {
		return read(text, styleOf(writer, styles).orElse(null));
	}
}
