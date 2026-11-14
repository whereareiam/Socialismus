package me.whereareiam.socialismus.service.phrase;

import me.whereareiam.socialismus.model.phrase.Phrases;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * The phrases of {@code phrases.yml} and of the modules' own {@code phrases.yml}: texts other config refers to by
 * key, as {@code <ref:key>}, and features tell players. A feature binds its scope once, such as {@code chat} or
 * {@code dialogue}, and looks its keys up there: in the scope first, then without it, in Socialismus's file and then
 * in each module's.
 *
 * <pre>{@code
 * # chat/list/staff.yml
 * requires:
 *   - permission: socialismus.chat.staff
 *     denied: "<ref:staff-only>"         # chat.staff-only, then staff-only
 * }</pre>
 *
 * <pre>{@code
 * PhraseScope chat = phrasebook.scope("chat");
 * chat.phrase("slow-down", Map.of("seconds", "3"))
 *         .ifPresent(phrase -> player.sendMessage(Serializer.serialize(phrase.toContent(player))));
 * }</pre>
 */
public interface Phrasebook {
	/**
	 * The phrases of a scope. A scope holds nothing of its own: it reads the files as they are at each call, so it
	 * stays valid across reloads and while modules add and remove their phrases.
	 *
	 * @param scope the scope, such as {@code chat}; empty for none, so a key is looked up only as it is written
	 * @return the phrases of the scope
	 */
	@NotNull PhraseScope scope(@NotNull String scope);

	/**
	 * Adds a module's phrases, looked up after Socialismus's own and those added before. A module keeps its keys
	 * under its id, such as {@code dialogue.denied.sender.permission.missing}, and the defaults of its condition
	 * types under {@code denied.<type>}, such as {@code denied.essentials:playtime.below}. Adding phrases for an
	 * owner whose phrases are added already replaces them, in their place, and the source added before closes
	 * nothing anymore.
	 *
	 * <pre>{@code
	 * PhraseSource source = phrasebook.add("dialogue", phrases::get);   // in enable
	 * source.close();                                                   // in disable
	 * }</pre>
	 *
	 * @param owner   the module's id
	 * @param phrases the module's phrases as they are now, read again after each reload
	 * @return the added phrases, closed when the module stops
	 */
	@NotNull PhraseSource add(@NotNull String owner, @NotNull Supplier<Phrases> phrases);
}
