package me.whereareiam.socialismus.service.style;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * The styles a place where players write accepts, such as a chat's {@code parameters.styles}, bound once by
 * {@link Styles#place}. It follows {@code styles.yml} through reloads until it is closed, as the place's owner does
 * when it binds the place again.
 */
public interface StylePlace extends AutoCloseable {
	/**
	 * Chooses the style of a writer: the last of the place's styles whose conditions they meet.
	 *
	 * @param writer the player who writes
	 * @return the style, {@link TextStyle#PLAIN} when they meet none or the place accepts none
	 */
	@NotNull TextStyle of(@NotNull SocialismusPlayer writer);

	/**
	 * Stops following {@code styles.yml}.
	 */
	@Override
	void close();
}
