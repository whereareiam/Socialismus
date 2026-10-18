package me.whereareiam.socialismus.model.requirement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Why a player does not meet a requirement, told to that player: a message template with the placeholders it is
 * filled with. It stays a template until it is shown, so it is serialized for whoever receives it, with that
 * receiver's own placeholders, like every other message of Socialismus.
 *
 * <pre>{@code
 * RequirementReason reason = RequirementReason.of(
 *         "<red>You are muted until {until}.",
 *         Map.of("until", "12:30")
 * );
 * player.sendMessage(Serializer.serialize(reason.toContent(player)));
 * }</pre>
 *
 * @see RequirementResult
 */
@Getter
@ToString
@EqualsAndHashCode
public final class RequirementReason {
	/**
	 * The message template, such as {@code "This only works in {worlds}."}.
	 */
	private final @NotNull String message;

	/**
	 * The values of the template's placeholders, by their key without braces.
	 */
	private final @NotNull Map<String, String> placeholders;

	private RequirementReason(@NotNull String message, @NotNull Map<String, String> placeholders) {
		if (message.isBlank()) throw new IllegalArgumentException("A requirement reason needs a message");

		this.message = message;
		this.placeholders = Map.copyOf(placeholders);
	}

	/**
	 * Creates a reason without placeholders of its own.
	 *
	 * @param message the message template; never blank
	 * @return the reason
	 * @throws IllegalArgumentException when the message is blank
	 */
	public static @NotNull RequirementReason of(@NotNull String message) {
		return new RequirementReason(message, Map.of());
	}

	/**
	 * Creates a reason whose template is filled with the given placeholders.
	 *
	 * @param message      the message template; never blank
	 * @param placeholders the values of its placeholders, by their key without braces
	 * @return the reason
	 * @throws IllegalArgumentException when the message is blank
	 */
	public static @NotNull RequirementReason of(@NotNull String message, @NotNull Map<String, String> placeholders) {
		return new RequirementReason(message, placeholders);
	}

	/**
	 * Returns a reason that says the given message instead, with this reason's placeholders, so a replacement
	 * written by an admin can still name what the original named.
	 *
	 * @param message the new message template; never blank
	 * @return the reason with the new message
	 * @throws IllegalArgumentException when the message is blank
	 */
	public @NotNull RequirementReason withMessage(@NotNull String message) {
		return new RequirementReason(message, placeholders);
	}

	/**
	 * Returns the content that serializes this reason for a receiver, for {@code Serializer.serialize}.
	 *
	 * @param receiver the player the reason is shown to
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
