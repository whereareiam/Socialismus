package me.whereareiam.socialismus.service.style;

import me.whereareiam.socialismus.exception.StyleException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The styles of {@code styles.yml}, which decide what players may format in what they write. A place where players
 * write binds the styles it accepts once, with its path, the way it binds its conditions, and then asks it for the
 * style of each writer:
 *
 * <pre>{@code
 * StylePlace place = styles.place("bubbles.default.styles", bubble.getStyles());
 * Component content = place.of(sender).read(text);
 * }</pre>
 *
 * <p>Text read somewhere the writer's conditions cannot be checked, such as on another server of a network, carries
 * the id of the style chosen where it was written, and is read with {@link #style(String)} there:</p>
 *
 * <pre>{@code
 * message.setStyle(place.of(sender).id());                         // where the player wrote
 * Component content = styles.style(message.getStyle()).read(text); // wherever it is shown
 * }</pre>
 *
 * <p>Tags that act on whoever reads a message or make the server look something up ({@code click}, {@code insert},
 * {@code nbt}, {@code selector}, {@code score}, {@code newline}, {@code reset}) are never read, whatever a style
 * allows.</p>
 */
public interface Styles {
	/**
	 * Binds the styles a place accepts.
	 *
	 * @param path     where the list is, such as {@code chats.global.parameters.styles}, for the failure
	 * @param accepted the ids of the styles, from lowest to highest
	 * @return the place
	 * @throws StyleException naming the path of an id no style has, once the styles are bound; a place bound before
	 *                        then is checked when they are
	 */
	@NotNull StylePlace place(@NotNull String path, @NotNull List<String> accepted);

	/**
	 * Finds a style by its id, for text whose style was chosen elsewhere.
	 *
	 * @param id the style's id
	 * @return the style, {@link TextStyle#PLAIN} when no style has the id, such as one another server defines
	 */
	@NotNull TextStyle style(@NotNull String id);
}
