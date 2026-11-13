package me.whereareiam.socialismus.common.chat.condition;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.conditia.model.Situation;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.Phrasebook;
import me.whereareiam.socialismus.service.chat.ChatConditions;
import me.whereareiam.socialismus.service.chat.ChatPlaces;
import me.whereareiam.socialismus.service.style.StylePlace;
import me.whereareiam.socialismus.service.style.Styles;
import me.whereareiam.socialismus.service.style.TextStyle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.whereareiam.socialismus.model.condition.ConditionParts.MESSAGE;
import static me.whereareiam.socialismus.model.condition.ConditionParts.RECIPIENT;
import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;

/**
 * The places of one chat, bound together and closed together when the chat is replaced. The chat's {@code requires}
 * and each format's take the parts sender and message, the chat's {@code reaches} the parts recipient, sender and
 * message.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class BoundChatPlaces implements ChatPlaces, AutoCloseable {
	private final Place requires;
	private final Place reaches;
	private final List<Format> formats;
	private final StylePlace styles;

	/**
	 * Binds the places of a chat.
	 *
	 * @param conditions binds the places
	 * @param phrases    checks the phrases the lists' texts refer to
	 * @param styles     binds the styles the chat accepts
	 * @param chat       the chat
	 * @return the chat's places
	 * @throws me.whereareiam.conditia.exception.ConditionException when one of its lists cannot be bound, or refers to
	 *                                                              a phrase no file has; the places bound before are
	 *                                                              closed
	 * @throws me.whereareiam.socialismus.exception.StyleException when the chat accepts a style no one defines
	 */
	static BoundChatPlaces bind(Conditions conditions, Phrasebook phrases, Styles styles, Chat chat) {
		String path = "chats." + chat.getId();
		List<Place> bound = new ArrayList<>();
		try {
			Place requires = open(bound, phrases, conditions.place(path + ".requires", listOf(chat.getRequires()), SENDER, MESSAGE));
			Place reaches = open(bound, phrases, conditions.place(path + ".reaches", listOf(chat.getReaches()), RECIPIENT, SENDER, MESSAGE));

			List<Format> formats = new ArrayList<>();
			List<ChatFormat> chatFormats = chat.getFormats() == null ? List.of() : chat.getFormats();
			for (int index = 0; index < chatFormats.size(); index++) {
				String at = path + ".formats[" + index + "].requires";
				ChatFormat format = chatFormats.get(index);
				formats.add(new Format(format, open(bound, phrases, conditions.place(at, listOf(format.getRequires()), SENDER, MESSAGE))));
			}

			StylePlace accepted = styles.place(path + ".parameters.styles", chat.getParameters().getStyles());

			return new BoundChatPlaces(requires, reaches, List.copyOf(formats), accepted);
		} catch (RuntimeException failure) {
			bound.forEach(Place::close);
			throw failure;
		}
	}

	static Requirements listOf(Requirements requires) {
		return requires == null ? Requirements.none() : requires;
	}

	private static Place open(List<Place> bound, Phrasebook phrases, Place place) {
		bound.add(phrases.checked(ChatConditions.PHRASE_SCOPE, place));

		return place;
	}

	@Override
	public @NotNull Verdict writes(@NotNull ChatMessage message) {
		return requires.check(Situation.of(SENDER, message.getSender()).and(MESSAGE, message));
	}

	@Override
	public @NotNull Verdict reaches(@NotNull SocialismusPlayer recipient, @NotNull ChatMessage message) {
		return reaches.check(Situation.of(RECIPIENT, recipient)
				.and(SENDER, message.getSender())
				.and(MESSAGE, message));
	}

	@Override
	public @NotNull Optional<ChatFormat> format(@NotNull ChatMessage message) {
		Situation situation = Situation.of(SENDER, message.getSender()).and(MESSAGE, message);
		for (int index = formats.size() - 1; index >= 0; index--)
			if (formats.get(index).requires().check(situation).passed()) return Optional.of(formats.get(index).format());

		return Optional.empty();
	}

	@Override
	public @NotNull TextStyle style(@NotNull SocialismusPlayer sender) {
		return styles.of(sender);
	}

	@Override
	public void close() {
		requires.close();
		reaches.close();
		formats.forEach(format -> format.requires().close());
		styles.close();
	}

	/**
	 * A format of the chat with the place of its {@code requires}.
	 */
	private record Format(ChatFormat format, Place requires) {
	}
}
