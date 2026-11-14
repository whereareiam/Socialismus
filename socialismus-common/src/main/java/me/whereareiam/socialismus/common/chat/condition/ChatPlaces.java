package me.whereareiam.socialismus.common.chat.condition;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.service.phrase.PhraseScope;
import me.whereareiam.socialismus.service.style.StylePlace;
import me.whereareiam.socialismus.service.style.Styles;

import java.util.ArrayList;
import java.util.List;

import static me.whereareiam.socialismus.model.condition.ConditionParts.MESSAGE;
import static me.whereareiam.socialismus.model.condition.ConditionParts.RECIPIENT;
import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;

/**
 * The places of one chat: who may write in it, whom it reaches, which of its formats a sender gets, and the style
 * they write with. They are bound together and closed together when the chat is replaced. The chat's {@code requires} and each format's take
 * the parts sender and message, the chat's {@code reaches} the parts recipient, sender and message.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ChatPlaces implements AutoCloseable {
	private final Place requires;
	private final Place reaches;
	private final List<Place> formats;
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
	static ChatPlaces bind(Conditions conditions, PhraseScope phrases, Styles styles, Chat chat) {
		String path = "chats." + chat.getId();
		List<Place> bound = new ArrayList<>();
		try {
			Place requires = open(bound, phrases, conditions.place(path + ".requires", listOf(chat.getRequires()), SENDER, MESSAGE));
			Place reaches = open(bound, phrases, conditions.place(path + ".reaches", listOf(chat.getReaches()), RECIPIENT, SENDER, MESSAGE));

			List<Place> formats = new ArrayList<>();
			List<ChatFormat> chatFormats = chat.getFormats() == null ? List.of() : chat.getFormats();
			for (int index = 0; index < chatFormats.size(); index++) {
				String at = path + ".formats[" + index + "].requires";
				formats.add(open(bound, phrases, conditions.place(at, listOf(chatFormats.get(index).getRequires()), SENDER, MESSAGE)));
			}

			StylePlace accepted = styles.place(path + ".parameters.styles", chat.getParameters().getStyles());

			return new ChatPlaces(requires, reaches, List.copyOf(formats), accepted);
		} catch (RuntimeException failure) {
			bound.forEach(Place::close);
			throw failure;
		}
	}

	static Requirements listOf(Requirements requires) {
		return requires == null ? Requirements.none() : requires;
	}

	private static Place open(List<Place> bound, PhraseScope phrases, Place place) {
		bound.add(phrases.checked(place));

		return place;
	}

	@Override
	public void close() {
		requires.close();
		reaches.close();
		formats.forEach(Place::close);
		styles.close();
	}
}
