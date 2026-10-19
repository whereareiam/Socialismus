package me.whereareiam.socialismus.module.dialogue.common.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.dialogue.api.PreferencesStore;
import me.whereareiam.socialismus.module.dialogue.api.model.DialoguePreferences;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * The preferences store {@link DialogueStores} chose.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SelectedPreferencesStore implements PreferencesStore {
	private final DialogueStores stores;

	@Override
	public @NotNull DialoguePreferences get(@NotNull UUID player) {
		return stores.preferences().get(player);
	}

	@Override
	public @NotNull DialoguePreferences update(@NotNull UUID player, @NotNull UnaryOperator<DialoguePreferences> change) {
		return stores.preferences().update(player, change);
	}
}
