package me.whereareiam.socialismus.module.network.common.node;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * The nodes of the network, as each announces itself: every node writes its own entry with one lease lifetime and
 * writes it again before then, so a node that stops leaves the directory within one lifetime.
 */
public final class NodeDirectory {
	/**
	 * The cache the announcements are kept in.
	 */
	public static final String CACHE = "socialismus:nodes";

	private final ReplicatedCache<NodeRecord> records;
	private final Duration lifetime;
	private final AtomicReference<NetworkNode> self;

	/**
	 * Creates the directory on a replicated cache, logging the nodes that join and leave.
	 *
	 * @param records  the cache that keeps the announcements
	 * @param self     what this node announces
	 * @param lifetime how long an announcement lives without being renewed
	 */
	public NodeDirectory(
			@NotNull ReplicatedCache<NodeRecord> records,
			@NotNull NetworkNode self,
			@NotNull Duration lifetime
	) {
		this.records = records;
		this.lifetime = lifetime;
		this.self = new AtomicReference<>(self);
		// An announcement that expired is forgotten locally, so the node it names is told apart by its key.
		records.onChange((key, previous, current, origin) -> {
			if (key.equals(this.self.get().getId().value())) return;
			if (previous == null && current != null) Logger.info("Node %s joined the network", key);
			if (previous != null && current == null) Logger.info("Node %s left the network", key);
		});
	}

	/**
	 * Returns this node as it announces itself.
	 */
	public @NotNull NetworkNode self() {
		return self.get();
	}

	/**
	 * Changes what this node announces, such as once it learned its server's name, and announces it right away.
	 */
	public void update(@NotNull NetworkNode node) {
		if (Objects.equals(self.getAndSet(node), node)) return;

		announce();
	}

	/**
	 * Returns the nodes this node knows of, itself included.
	 */
	public @NotNull Collection<NetworkNode> nodes() {
		NetworkNode current = self.get();
		Stream<NetworkNode> others = records.entries().values().stream()
				.map(NodeDirectory::toNode)
				.filter(Objects::nonNull)
				.filter(node -> !node.getId().equals(current.getId()));

		return Stream.concat(Stream.of(current), others).toList();
	}

	/**
	 * Announces this node for one more lifetime. Called a few times per lifetime.
	 */
	public void announce() {
		NetworkNode node = self.get();
		records.put(node.getId().value(), NodeRecord.of(node), lifetime);
	}

	/**
	 * Takes this node out of the directory, such as when it stops.
	 */
	public void leave() {
		records.remove(self.get().getId().value());
	}

	private static NetworkNode toNode(NodeRecord record) {
		try {
			return record.toNode();
		} catch (RuntimeException failure) {
			return null;
		}
	}
}
