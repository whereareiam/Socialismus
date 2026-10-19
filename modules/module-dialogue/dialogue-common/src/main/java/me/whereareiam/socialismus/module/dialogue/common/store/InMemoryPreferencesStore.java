package me.whereareiam.socialismus.module.dialogue.common.store;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.dialogue.api.PreferencesStore;
import me.whereareiam.socialismus.module.dialogue.api.model.DialoguePreferences;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

/**
 * Keeps players' preferences in the server's memory, so they last until it stops and stay on this server.
 */
@Singleton
public class InMemoryPreferencesStore implements PreferencesStore {
	private final Map<UUID, DialoguePreferences> preferences = new ConcurrentHashMap<>();

	@Override
	public @NotNull DialoguePreferences get(@NotNull UUID player) {
		return preferences.getOrDefault(player, DialoguePreferences.NONE);
	}

	@Override
	public @NotNull DialoguePreferences update(@NotNull UUID player, @NotNull UnaryOperator<DialoguePreferences> change) {
		DialoguePreferences[] result = new DialoguePreferences[1];
		preferences.compute(player, (id, current) -> {
			result[0] = change.apply(current == null ? DialoguePreferences.NONE : current);
			return DialoguePreferences.NONE.equals(result[0]) ? null : result[0];
		});

		return result[0];
	}
}
