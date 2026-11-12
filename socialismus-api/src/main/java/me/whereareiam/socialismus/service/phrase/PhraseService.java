package me.whereareiam.socialismus.service.phrase;

import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.exception.ConditionException;
import me.whereareiam.socialismus.model.phrase.Phrases;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The phrases of {@code phrases.yml} and of the modules' own {@code phrases.yml}: texts other config refers to by
 * key, as {@code <ref:key>}. A key is looked up in a scope first, such as {@code chat} or {@code dialogue}, then
 * without it, in Socialismus's file and then in each module's:
 *
 * <pre>{@code
 * # chat/list/staff.yml
 * requires:
 *   - permission: socialismus.chat.staff
 *     denied: "<ref:staff-only>"         # chat.staff-only, then staff-only
 * }</pre>
 *
 * <pre>{@code
 * String text = phrases.render("chat", "<ref:staff-only> <gray>Ask a moderator.");
 * }</pre>
 */
public interface PhraseService {
	/**
	 * Finds the text of a key, in the scope first.
	 *
	 * @param scope the scope, such as {@code chat}; empty for none
	 * @param key   the key, such as {@code denied.world.outside}
	 * @return the text as written, its references not yet replaced; empty when no file has it
	 */
	@NotNull Optional<String> find(@NotNull String scope, @NotNull String key);

	/**
	 * Replaces every {@code <ref:key>} in a text with the phrase it names, found in the scope, and the references in
	 * that phrase in turn. A reference nothing resolves, or one that refers back to itself, is left out.
	 *
	 * @param scope the scope, such as {@code chat}; empty for none
	 * @param text  the text, such as a line's {@code denied}
	 * @return the text with its references replaced
	 */
	@NotNull String render(@NotNull String scope, @NotNull String text);

	/**
	 * The references of a text that nothing resolves in the scope, so a feature can refuse config that refers to
	 * phrases no file has. References inside the phrases are followed.
	 *
	 * @param scope the scope, such as {@code chat}; empty for none
	 * @param text  the text
	 * @return the keys of the references nothing resolves, empty when every one does
	 */
	@NotNull List<String> unresolved(@NotNull String scope, @NotNull String text);

	/**
	 * Refuses a place whose lines refer to phrases nothing resolves, so a typo in {@code <ref:...>} is found when the
	 * place is bound, with its config path, not in front of a player.
	 *
	 * @param scope the scope the place's texts are told in, such as {@code chat}
	 * @param place the place, closed when a reference resolves nowhere
	 * @return the place
	 * @throws ConditionException when a reference resolves nowhere, naming the path of the text
	 */
	default @NotNull Place checked(@NotNull String scope, @NotNull Place place) {
		for (Map.Entry<String, String> text : place.wording().entrySet()) {
			List<String> missing = unresolved(scope, text.getValue());
			if (missing.isEmpty()) continue;

			place.close();
			String where = scope.isEmpty() ? "" : " or " + scope + "." + missing.getFirst();
			throw new ConditionException(text.getKey(), "no phrase is named " + missing.getFirst() + where);
		}

		return place;
	}

	/**
	 * Adds a module's phrases, looked up after Socialismus's own and those added before. A module keeps its keys
	 * under its id, such as {@code dialogue.denied.permission.missing}, and the defaults of its condition types
	 * under {@code denied.<type>}, such as {@code denied.essentials:playtime.below}.
	 *
	 * @param owner   the module's id
	 * @param phrases the module's phrases as they are now, read again after each reload
	 */
	void add(@NotNull String owner, @NotNull Supplier<Phrases> phrases);

	/**
	 * Removes a module's phrases, when it stops.
	 *
	 * @param owner the module's id
	 */
	void remove(@NotNull String owner);
}
