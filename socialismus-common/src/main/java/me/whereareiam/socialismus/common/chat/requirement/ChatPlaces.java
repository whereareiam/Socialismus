package me.whereareiam.socialismus.common.chat.requirement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatFormat;
import me.whereareiam.socialismus.service.chat.ChatRequirementService;
import me.whereareiam.socialismus.service.Phrasebook;

import java.util.ArrayList;
import java.util.List;

import static me.whereareiam.socialismus.model.requirement.RequirementParts.MESSAGE;
import static me.whereareiam.socialismus.model.requirement.RequirementParts.RECIPIENT;
import static me.whereareiam.socialismus.model.requirement.RequirementParts.SENDER;

/**
 * The places of one chat: who may write in it, whom it reaches, and which of its formats a sender gets. They are
 * bound together and closed together when the chat is replaced. The chat's {@code requires} and each format's take
 * the parts sender and message, the chat's {@code reaches} the parts recipient, sender and message.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ChatPlaces implements AutoCloseable {
	private final Place requires;
	private final Place reaches;
	private final List<Place> formats;

	/**
	 * Binds the places of a chat.
	 *
	 * @param conditions binds the places
	 * @param phrases    checks the phrases the lists' texts refer to
	 * @param chat       the chat
	 * @return the chat's places
	 * @throws me.whereareiam.conditia.exception.ConditionException when one of its lists cannot be bound, or refers to
	 *                                                              a phrase no file has; the places bound before are
	 *                                                              closed
	 */
	static ChatPlaces bind(Conditions conditions, Phrasebook phrases, Chat chat) {
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

			return new ChatPlaces(requires, reaches, List.copyOf(formats));
		} catch (RuntimeException failure) {
			bound.forEach(Place::close);
			throw failure;
		}
	}

	static Requirements listOf(Requirements requires) {
		return requires == null ? Requirements.none() : requires;
	}

	private static Place open(List<Place> bound, Phrasebook phrases, Place place) {
		bound.add(phrases.checked(ChatRequirementService.PHRASE_SCOPE, place));

		return place;
	}

	@Override
	public void close() {
		requires.close();
		reaches.close();
		formats.forEach(Place::close);
	}
}
