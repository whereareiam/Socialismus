package me.whereareiam.socialismus.module.dialogue.common.condition;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.service.phrase.PhraseScope;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static me.whereareiam.socialismus.model.condition.ConditionParts.RECIPIENT;
import static me.whereareiam.socialismus.model.condition.ConditionParts.SENDER;

/**
 * One generation of Dialogue's places, bound from its settings and formats and closed together when they are
 * reloaded: who may message whom, the privileges of senders, and each format, found by the format itself.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class DialoguePlaces implements AutoCloseable {
	private final Place requires;
	private final Place reaches;
	private final boolean reachesAnyone;
	private final Place bypassesToggle;
	private final Place exemptFromIgnore;
	private final Place hiddenFromSpies;
	private final Map<DialogueFormat, FormatPlaces> formats;

	/**
	 * Binds the places of Dialogue's settings and formats.
	 *
	 * @param conditions binds the places
	 * @param phrases    checks the phrases the lists' texts refer to
	 * @param settings   Dialogue's settings
	 * @param formats    the lists of formats by their path, such as {@code formats.messaging.sent}
	 * @return the places
	 * @throws me.whereareiam.conditia.exception.ConditionException when a list cannot be bound; the places bound
	 *                                                              before are closed
	 */
	static DialoguePlaces bind(Conditions conditions, PhraseScope phrases, DialogueSettings settings, Map<String, List<DialogueFormat>> formats) {
		Binding binding = new Binding(conditions, phrases);
		try {
			Requirements reaches = listOf(settings.getMessaging().getReaches());
			DialoguePlaces places = new DialoguePlaces(
					binding.open("messaging.requires", settings.getMessaging().getRequires(), false),
					binding.open("messaging.reaches", reaches, true),
					reaches.getConditions().items().isEmpty(),
					binding.open("control.toggle.bypass", settings.getControl().getToggle().getBypass(), false),
					binding.open("control.ignore.exempt", settings.getControl().getIgnore().getExempt(), false),
					binding.open("moderation.spy.exempt", settings.getModeration().getSpy().getExempt(), false),
					new IdentityHashMap<>()
			);
			formats.forEach((path, list) -> {
				for (int index = 0; list != null && index < list.size(); index++)
					places.formats.put(list.get(index), binding.format(path + "[" + index + "]", list.get(index)));
			});

			return places;
		} catch (RuntimeException failure) {
			binding.bound.forEach(Place::close);
			throw failure;
		}
	}

	private static Requirements listOf(Requirements requires) {
		return requires == null ? Requirements.none() : requires;
	}

	@Override
	public void close() {
		requires.close();
		reaches.close();
		bypassesToggle.close();
		exemptFromIgnore.close();
		hiddenFromSpies.close();
		formats.values().forEach(FormatPlaces::close);
	}

	/**
	 * The places of one format: what the sender and what the recipient must meet.
	 *
	 * @param requires      the format's {@code requires}, with the part sender
	 * @param reaches       the format's {@code reaches}, with the parts recipient and sender
	 * @param reachesAnyone whether the format's {@code reaches} is empty, so a recipient on another server meets it
	 */
	record FormatPlaces(Place requires, Place reaches, boolean reachesAnyone) {
		void close() {
			requires.close();
			reaches.close();
		}
	}

	private record Binding(Conditions conditions, PhraseScope phrases, List<Place> bound) {
		Binding(Conditions conditions, PhraseScope phrases) {
			this(conditions, phrases, new ArrayList<>());
		}

		Place open(String path, Requirements requires, boolean aboutRecipient) {
			Place place = aboutRecipient
					? conditions.place(path, listOf(requires), RECIPIENT, SENDER)
					: conditions.place(path, listOf(requires), SENDER);
			bound.add(phrases.checked(place));

			return place;
		}

		FormatPlaces format(String path, DialogueFormat format) {
			Requirements reaches = listOf(format.getReaches());

			return new FormatPlaces(
					open(path + ".requires", format.getRequires(), false),
					open(path + ".reaches", reaches, true),
					reaches.getConditions().items().isEmpty()
			);
		}
	}
}
