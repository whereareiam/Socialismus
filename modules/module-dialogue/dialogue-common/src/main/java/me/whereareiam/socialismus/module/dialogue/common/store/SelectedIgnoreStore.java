package me.whereareiam.socialismus.module.dialogue.common.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * The ignore store {@link DialogueStores} chose.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SelectedIgnoreStore implements IgnoreStore {
	private final DialogueStores stores;

	@Override
	public boolean ignores(@NotNull UUID player, @NotNull UUID other) {
		return stores.ignores().ignores(player, other);
	}

	@Override
	public @NotNull List<IgnoredPlayer> ignored(@NotNull UUID player) {
		return stores.ignores().ignored(player);
	}

	@Override
	public boolean ignore(@NotNull UUID player, @NotNull IgnoredPlayer other) {
		return stores.ignores().ignore(player, other);
	}

	@Override
	public boolean unignore(@NotNull UUID player, @NotNull UUID other) {
		return stores.ignores().unignore(player, other);
	}
}
