package me.whereareiam.socialismus.module.network.common.player;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.player.NetworkPlayer;
import me.whereareiam.socialismus.module.network.api.player.NetworkPlayers;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.registry.PlayerSource;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * The players of the network: each node announces the players it hosts, for one lease lifetime and again before then,
 * so the players of a node that stops disappear within one lifetime. A player a proxy and a server both host is
 * announced by both; the server's announcement wins, since it knows the player's world.
 */
public final class DefaultNetworkPlayers implements NetworkPlayers, PlayerSource {
	/**
	 * The cache the announcements are kept in, by the player's unique id and the announcing node.
	 */
	public static final String CACHE = "socialismus:players";

	private final ReplicatedCache<PlayerRecord> records;
	private final Supplier<NetworkNode> self;
	private final Duration lifetime;

	public DefaultNetworkPlayers(@NotNull ReplicatedCache<PlayerRecord> records, @NotNull Supplier<NetworkNode> self, @NotNull Duration lifetime) {
		this.records = records;
		this.self = self;
		this.lifetime = lifetime;
	}

	/**
	 * Announces a player this node hosts, or announces them again, such as after they changed servers.
	 */
	public void host(@NotNull SocialismusPlayer player) {
		NetworkNode node = self.get();
		records.put(key(player.getUniqueId(), node.getId()), new PlayerRecord(
				player.getUniqueId().toString(),
				player.getUsername(),
				node.getId().value(),
				node.getRole(),
				player.getServer()
		), lifetime);
	}

	/**
	 * Takes back the announcement of a player this node no longer hosts.
	 */
	public void unhost(@NotNull UUID player) {
		records.remove(key(player, self.get().getId()));
	}

	/**
	 * Takes back the announcements of every player this node hosts, such as when it stops.
	 */
	public void unhostAll() {
		String me = self.get().getId().value();
		records.entries().forEach((key, record) -> {
			if (me.equals(record.getNode())) records.remove(key);
		});
	}

	/**
	 * Announces every player this node hosts for one more lifetime. Called a few times per lifetime.
	 */
	public void renew(@NotNull Collection<SocialismusPlayer> hosted) {
		hosted.forEach(this::host);
	}

	/**
	 * Returns the server a proxy says a player this node hosts is on: how a server learns its own name.
	 *
	 * @param player a player this node hosts
	 * @return the server's name, or empty when no proxy announces the player
	 */
	public @NotNull Optional<String> serverByProxy(@NotNull UUID player) {
		return records.entries().values().stream()
				.filter(record -> record.getRole() == NodeRole.PROXY)
				.filter(record -> player.toString().equals(record.getUniqueId()))
				.map(PlayerRecord::getServer)
				.filter(server -> server != null && !server.isBlank())
				.findFirst();
	}

	/**
	 * Calls the listener whenever a proxy announces the server of a player, so a server learns its name as soon as
	 * the proxy in front of it says it, instead of at its next renewal.
	 *
	 * @param listener called with the player's unique id and the server the proxy announced
	 */
	public void onServerByProxy(@NotNull BiConsumer<UUID, String> listener) {
		records.onChange((key, previous, current, origin) -> {
			if (current == null || current.getRole() != NodeRole.PROXY) return;
			if (current.getServer() == null || current.getServer().isBlank()) return;

			try {
				listener.accept(UUID.fromString(current.getUniqueId()), current.getServer());
			} catch (IllegalArgumentException malformed) {
				// A record without a valid unique id names no player.
			}
		});
	}

	@Override
	public @NotNull Collection<NetworkPlayer> players() {
		NodeId me = self.get().getId();
		Map<String, PlayerRecord> best = new LinkedHashMap<>();
		records.entries().values().stream()
				.filter(record -> !me.value().equals(record.getNode()))
				// A server's announcement comes last, so it replaces a proxy's of the same player.
				.sorted(Comparator.comparing(record -> record.getRole() == NodeRole.SERVER))
				.forEach(record -> best.put(record.getUniqueId(), record));

		return best.values().stream().map(DefaultNetworkPlayers::player).flatMap(Optional::stream).toList();
	}

	@Override
	public @NotNull Optional<NetworkPlayer> find(@NotNull UUID uniqueId) {
		return players().stream().filter(player -> player.getUniqueId().equals(uniqueId)).findFirst();
	}

	@Override
	public @NotNull Optional<NetworkPlayer> find(@NotNull String username) {
		return players().stream().filter(player -> player.getUsername().equalsIgnoreCase(username)).findFirst();
	}

	@Override
	public @NotNull Optional<SocialismusPlayer> findPlayer(@NotNull UUID uniqueId) {
		return find(uniqueId).map(SocialismusPlayer.class::cast);
	}

	@Override
	public @NotNull Optional<SocialismusPlayer> findPlayer(@NotNull String username) {
		return find(username).map(SocialismusPlayer.class::cast);
	}

	@Override
	public @NotNull Collection<SocialismusPlayer> knownPlayers() {
		return List.copyOf(players());
	}

	private static Optional<NetworkPlayer> player(PlayerRecord record) {
		try {
			return Optional.of(new NetworkPlayer(UUID.fromString(record.getUniqueId()), record.getName(), NodeId.of(record.getNode()), record.getServer()));
		} catch (RuntimeException failure) {
			return Optional.empty();
		}
	}

	private static String key(UUID player, NodeId node) {
		return player + "@" + node.value();
	}
}
