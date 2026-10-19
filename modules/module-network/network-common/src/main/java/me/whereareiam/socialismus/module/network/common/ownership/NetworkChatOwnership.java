package me.whereareiam.socialismus.module.network.common.ownership;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.network.api.lease.Lease;
import me.whereareiam.socialismus.module.network.api.lease.Leases;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.service.chat.ChatOwnership;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Decides which node handles each player's chat, so it is handled exactly once on the network: the node that holds the
 * player's lease. A server claims the players it hosts with a higher priority than a proxy, since it can take a
 * message back while a proxy cannot; a proxy claims the players on servers that do not run Socialismus, and leaves
 * those on servers that do. When a server stops, its leases expire and the proxy takes its players over.
 */
public final class NetworkChatOwnership implements ChatOwnership {
	/**
	 * The name of the leases on the network.
	 */
	public static final String LEASES = "socialismus:chat-owners";

	static final int SERVER_PRIORITY = 10;
	static final int PROXY_PRIORITY = 1;

	private final Leases leases;
	private final Supplier<NetworkNode> self;
	private final Supplier<Collection<NetworkNode>> nodes;

	public NetworkChatOwnership(
			@NotNull Leases leases,
			@NotNull Supplier<NetworkNode> self,
			@NotNull Supplier<Collection<NetworkNode>> nodes
	) {
		this.leases = leases;
		this.self = self;
		this.nodes = nodes;
	}

	@Override
	public boolean handles(@NotNull SocialismusPlayer player) {
		Optional<Lease> holder = leases.holder(player.getUniqueId().toString());

		// Nobody claimed the player yet, such as right after they joined; handling them beats dropping their chat.
		return holder.map(lease -> lease.getHolder().equals(self.get().getId())).orElse(true);
	}

	/**
	 * Claims or gives up the players this node hosts, as their servers and the network's nodes are now. Called when a
	 * player joins and a few times per lease lifetime.
	 */
	public void review(@NotNull Collection<SocialismusPlayer> hosted) {
		hosted.forEach(this::review);
	}

	/**
	 * Claims or gives up one player this node hosts.
	 */
	public void review(@NotNull SocialismusPlayer player) {
		String key = player.getUniqueId().toString();
		NetworkNode node = self.get();
		if (node.getRole() == NodeRole.PROXY && serverRunsSocialismus(player.getServer())) {
			leases.release(key);
			return;
		}

		if (!leases.holds(key)) leases.claim(key, node.getRole() == NodeRole.SERVER ? SERVER_PRIORITY : PROXY_PRIORITY);
	}

	/**
	 * Gives up a player who left this node.
	 */
	public void release(@NotNull SocialismusPlayer player) {
		leases.release(player.getUniqueId().toString());
	}

	private boolean serverRunsSocialismus(String server) {
		if (server == null) return false;

		return nodes.get().stream()
				.filter(node -> node.getRole() == NodeRole.SERVER)
				.map(NetworkNode::getServer)
				.anyMatch(name -> Objects.equals(name, server));
	}
}
