package me.whereareiam.socialismus.model.phrase;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * A phrase ready to tell a player, such as why a condition refused them: its text, with references to other
 * phrases already filled in, and the placeholders it is filled with. It stays a template until it is shown, so it is
 * serialized for whoever receives it, with that receiver's own placeholders, like every other message of
 * Socialismus.
 *
 * <pre>{@code
 * Phrase phrase = Phrase.of(
 *         "<red>You are muted until {until}.",
 *         Map.of("until", "12:30")
 * );
 * player.sendMessage(Serializer.serialize(phrase.toContent(player)));
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
public final class Phrase {
	/**
	 * The message template, such as {@code "This only works in {worlds}."}.
	 */
	private final @NotNull String message;

	/**
	 * The values of the template's placeholders, by their key without braces.
	 */
	private final @NotNull Map<String, String> placeholders;

	private Phrase(@NotNull String message, @NotNull Map<String, String> placeholders) {
		if (message.isBlank()) throw new IllegalArgumentException("A phrase needs a message");

		this.message = message;
		this.placeholders = Map.copyOf(placeholders);
	}

	/**
	 * Creates a phrase without placeholders of its own.
	 *
	 * @param message the message template; never blank
	 * @return the phrase
	 * @throws IllegalArgumentException when the message is blank
	 */
	public static @NotNull Phrase of(@NotNull String message) {
		return new Phrase(message, Map.of());
	}

	/**
	 * Creates a phrase whose template is filled with the given placeholders.
	 *
	 * @param message      the message template; never blank
	 * @param placeholders the values of its placeholders, by their key without braces
	 * @return the phrase
	 * @throws IllegalArgumentException when the message is blank
	 */
	public static @NotNull Phrase of(@NotNull String message, @NotNull Map<String, String> placeholders) {
		return new Phrase(message, placeholders);
	}

	/**
	 * Returns a phrase that says the given message instead, with this phrase's placeholders, so a replacement
	 * written by an admin can still name what the original named.
	 *
	 * @param message the new message template; never blank
	 * @return the phrase with the new message
	 * @throws IllegalArgumentException when the message is blank
	 */
	public @NotNull Phrase withMessage(@NotNull String message) {
		return new Phrase(message, placeholders);
	}

	/**
	 * Returns the content that serializes this phrase for a receiver, for {@code Serializer.serialize}.
	 *
	 * @param receiver the player the phrase is shown to
	 * @return the content to serialize
	 */
	public @NotNull SerializerContent toContent(@NotNull Actor receiver) {
		return SerializerContent.builder()
				.receiver(receiver)
				.message(message)
				.placeholders(placeholders)
				.build();
	}
}
