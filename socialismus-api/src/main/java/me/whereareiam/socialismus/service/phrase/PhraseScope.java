package me.whereareiam.socialismus.service.phrase;

import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.socialismus.model.phrase.Phrase;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The phrases of one scope, such as {@code chat}, as {@link Phrasebook#scope} binds them: a key is looked up under
 * the scope first, such as {@code chat.staff-only}, then as it is written, such as {@code staff-only}.
 *
 * <pre>{@code
 * PhraseScope chat = phrasebook.scope("chat");
 * chat.render("<red><ref:staff-only>");
 * }</pre>
 */
public interface PhraseScope {
	/**
	 * The scope's name.
	 *
	 * @return the name, such as {@code chat}; empty for no scope
	 */
	@NotNull String name();

	/**
	 * Finds the text of a key, in the scope first.
	 *
	 * @param key the key, such as {@code denied.world.outside}
	 * @return the text as written, its references not yet replaced; empty when no file has it
	 */
	@NotNull Optional<String> find(@NotNull String key);

	/**
	 * The phrase of a key, ready to tell a player: found in the scope first, its references replaced.
	 *
	 * @param key          the key, such as {@code slow-down}
	 * @param placeholders the values of its placeholders, by their key without braces
	 * @return the phrase, empty when no file has the key or its text is blank
	 */
	default @NotNull Optional<Phrase> phrase(@NotNull String key, @NotNull Map<String, String> placeholders) {
		return find(key)
				.map(this::render)
				.filter(text -> !text.isBlank())
				.map(text -> Phrase.of(text, placeholders));
	}

	/**
	 * Replaces every {@code <ref:key>} in a text with the phrase it names, found in the scope, and the references in
	 * that phrase in turn. A reference nothing resolves, or one that refers back to itself, is left out.
	 *
	 * @param text the text, such as a line's {@code denied}
	 * @return the text with its references replaced
	 */
	@NotNull String render(@NotNull String text);

	/**
	 * The references of a text that nothing resolves in the scope, so a feature can refuse config that refers to
	 * phrases no file has. References inside the phrases are followed.
	 *
	 * @param text the text
	 * @return the keys of the references nothing resolves, empty when every one does
	 */
	@NotNull List<String> unresolved(@NotNull String text);

	/**
	 * Refuses a place whose lines refer to phrases nothing resolves, so a typo in {@code <ref:...>} is found when the
	 * place is bound, with its config path, not in front of a player.
	 *
	 * @param place the place, whose texts are told in this scope; closed when a reference resolves nowhere
	 * @return the place
	 * @throws ConditionException when a reference resolves nowhere, naming the path of the text
	 */
	default @NotNull Place checked(@NotNull Place place) {
		for (Map.Entry<String, String> text : place.wording().entrySet()) {
			List<String> missing = unresolved(text.getValue());
			if (missing.isEmpty()) continue;

			place.close();
			String where = name().isEmpty() ? "" : " or " + name() + "." + missing.getFirst();
			throw new ConditionException(text.getKey(), "no phrase is named " + missing.getFirst() + where);
		}

		return place;
	}
}
