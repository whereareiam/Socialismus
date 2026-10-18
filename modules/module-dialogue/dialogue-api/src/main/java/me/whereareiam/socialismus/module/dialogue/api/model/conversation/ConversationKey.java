package me.whereareiam.socialismus.module.dialogue.api.model.conversation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

/**
 * Names the conversation of two players, whatever order and casing their names are given in.
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class ConversationKey {
	/**
	 * The participants' names in lower case, sorted and joined by {@code :}.
	 */
	private final String key;

	/**
	 * Returns the key of two players.
	 *
	 * @param player1 the name of one player
	 * @param player2 the name of the other player
	 * @return the same key for either order and casing
	 */
	public static @NotNull ConversationKey of(@NotNull String player1, @NotNull String player2) {
		return of(Set.of(player1, player2));
	}

	/**
	 * Returns the key of the given players.
	 *
	 * @param participants the names of the players
	 * @return the same key for any order and casing
	 */
	public static @NotNull ConversationKey of(@NotNull Set<String> participants) {
		Set<String> sorted = new TreeSet<>();
		participants.forEach(p -> sorted.add(p.toLowerCase()));

		return new ConversationKey(String.join(":", sorted));
	}
}
