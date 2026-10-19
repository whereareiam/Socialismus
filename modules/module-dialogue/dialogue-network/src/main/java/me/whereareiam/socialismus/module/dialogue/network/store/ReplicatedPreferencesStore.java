package me.whereareiam.socialismus.module.dialogue.network.store;

import me.whereareiam.socialismus.module.dialogue.api.PreferencesStore;
import me.whereareiam.socialismus.module.dialogue.api.model.DialoguePreferences;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.module.network.api.replication.ReplicationSystem;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Players' preferences, shared by every node of the network: one entry per player who decided something.
 */
public final class ReplicatedPreferencesStore implements PreferencesStore {
	static final String CACHE = "dialogue:preferences";

	private final ReplicatedCache<PreferencesRecord> records;

	public ReplicatedPreferencesStore(@NotNull ReplicationSystem replication) {
		this.records = replication.cache(CACHE, PreferencesRecord.class);
	}

	@Override
	public @NotNull DialoguePreferences get(@NotNull UUID player) {
		return records.get(player.toString()).map(PreferencesRecord::toPreferences).orElse(DialoguePreferences.NONE);
	}

	@Override
	public @NotNull DialoguePreferences update(@NotNull UUID player, @NotNull UnaryOperator<DialoguePreferences> change) {
		PreferencesRecord written = ConditionalWrites.change(records, player.toString(), current -> {
			DialoguePreferences changed = change.apply(current == null ? DialoguePreferences.NONE : current.toPreferences());
			return DialoguePreferences.NONE.equals(changed) ? null : PreferencesRecord.of(changed);
		}, null);

		return written == null ? DialoguePreferences.NONE : written.toPreferences();
	}
}
