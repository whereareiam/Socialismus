package me.whereareiam.socialismus.exception;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when styles cannot be bound: a style of {@code styles.yml} names a tag no style may allow, or a place lists
 * a style no one defines. The path names where, such as {@code styles[1].tags[2]} or
 * {@code chats.global.parameters.styles[0]}.
 */
@Getter
public class StyleException extends RuntimeException {
	/**
	 * Where the mistake is.
	 */
	private final @NotNull String path;

	/**
	 * @param path    where the mistake is
	 * @param message what is wrong there
	 */
	public StyleException(@NotNull String path, @NotNull String message) {
		super(path + ": " + message);
		this.path = path;
	}
}
