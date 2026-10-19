package me.whereareiam.socialismus.module.dialogue.api;

import me.whereareiam.socialismus.module.dialogue.api.model.DialoguePreferences;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * The {@link DialoguePreferences} of each player, by their unique id. Dialogue keeps them in memory; with
 * SocialismusNetwork, every node of the network shares them.
 *
 * <pre>{@code
 * boolean off = preferences.update(player, current -> current.withMessagesOff(!current.isMessagesOff())).isMessagesOff();
 * }</pre>
 */
public interface PreferencesStore {
	/**
	 * Returns a player's preferences, {@link DialoguePreferences#NONE} when they decided nothing.
	 */
	@NotNull DialoguePreferences get(@NotNull UUID player);

	/**
	 * Changes a player's preferences. The change is applied to their preferences as they are when it is applied, so
	 * two changes of different preferences at the same moment both take effect.
	 *
	 * @param player the player
	 * @param change turns their preferences into the new ones
	 * @return their preferences after the change
	 */
	@NotNull DialoguePreferences update(@NotNull UUID player, @NotNull UnaryOperator<DialoguePreferences> change);
}
