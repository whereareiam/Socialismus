package me.whereareiam.socialismus.module.dialogue.network.store;

import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.module.network.api.replication.ReplicationSystem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Whom each player ignores, shared by every node of the network: one entry per player who ignores anyone.
 */
public final class ReplicatedIgnoreStore implements IgnoreStore {
	static final String CACHE = "dialogue:ignores";

	private final ReplicatedCache<IgnoreRecord> records;

	public ReplicatedIgnoreStore(@NotNull ReplicationSystem replication) {
		this.records = replication.cache(CACHE, IgnoreRecord.class);
	}

	@Override
	public boolean ignores(@NotNull UUID player, @NotNull UUID other) {
		return ignored(player).stream().anyMatch(ignored -> ignored.getUniqueId().equals(other));
	}

	@Override
	public @NotNull List<IgnoredPlayer> ignored(@NotNull UUID player) {
		return records.get(player.toString())
				.map(record -> record.getIgnored().stream()
						.map(entry -> new IgnoredPlayer(UUID.fromString(entry.getUniqueId()), entry.getName()))
						.toList())
				.orElse(List.of());
	}

	@Override
	public boolean ignore(@NotNull UUID player, @NotNull IgnoredPlayer other) {
		AtomicBoolean added = new AtomicBoolean();
		ConditionalWrites.change(records, player.toString(), current -> {
			List<IgnoreRecord.Entry> ignored = current == null ? new ArrayList<>() : new ArrayList<>(current.getIgnored());
			added.set(ignored.stream().noneMatch(entry -> entry.getUniqueId().equals(other.getUniqueId().toString())));
			if (added.get()) ignored.add(new IgnoreRecord.Entry(other.getUniqueId().toString(), other.getName()));

			return new IgnoreRecord(ignored);
		}, null);

		return added.get();
	}

	@Override
	public boolean unignore(@NotNull UUID player, @NotNull UUID other) {
		AtomicBoolean removed = new AtomicBoolean();
		ConditionalWrites.change(records, player.toString(), current -> {
			if (current == null) return null;

			List<IgnoreRecord.Entry> ignored = new ArrayList<>(current.getIgnored());
			removed.set(ignored.removeIf(entry -> entry.getUniqueId().equals(other.toString())));

			return ignored.isEmpty() ? null : new IgnoreRecord(ignored);
		}, null);

		return removed.get();
	}
}
