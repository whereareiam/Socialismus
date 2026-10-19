package me.whereareiam.socialismus.module.network;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerAddedEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerRemovedEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerServerChangedEvent;
import me.whereareiam.socialismus.event.resource.ResourceRegisteredEvent;
import me.whereareiam.socialismus.event.resource.ResourceUnregisteredEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.network.api.NetworkResources;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.module.network.common.CommonConfiguration;
import me.whereareiam.socialismus.module.network.common.DefaultNetworkService;
import me.whereareiam.socialismus.module.network.common.chat.ChatRelay;
import me.whereareiam.socialismus.module.network.common.config.NetworkSettings;
import me.whereareiam.socialismus.module.network.common.config.defaults.NetworkSettingsDefaults;
import me.whereareiam.socialismus.module.network.common.config.migration.CoreSyncImport;
import me.whereareiam.socialismus.module.network.common.config.migration.NetworkConfigMigrations;
import me.whereareiam.socialismus.module.network.common.ownership.NetworkChatOwnership;
import me.whereareiam.socialismus.module.network.common.replication.DefaultReplicationSystem;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.chat.ChatCoordinationService;
import me.whereareiam.socialismus.service.chat.ChatHistoryService;
import me.whereareiam.socialismus.service.chat.ChatOwnershipService;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.strata.Strata;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Connects this Socialismus to the others of the network: it shares this node's players and their chat ownership,
 * relays chat and chat history, and registers the network for the modules that replicate. It replicates through the
 * transport a transport module registers, such as SocialismusRedis; without one this node is alone on the network.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SocialismusNetwork extends SocialisticModule implements ResourceProvider, EventListener {
	private final Registry<Reloadable> reloadableRegistry;
	private final SerializationService serializationService;
	private final ResourceRegistry resourceRegistry;
	private final EventManager eventManager;
	private final PlayerRegistry playerRegistry;
	private final ChatOwnershipService chatOwnershipService;
	private final ChatCoordinationService chatCoordinationService;
	private final ChatContainerService chatContainerService;
	private final ChatHistoryService chatHistoryService;

	private ScheduledExecutorService executor;
	private DefaultNetworkService network;
	private NetworkChatOwnership ownership;
	private Duration lifetime;

	@Override
	public void onLoad() {
		new Strata(List.of(NetworkConfigMigrations.on(Config.configured(), workingPath))).migrate();
		new CoreSyncImport(Config.configured(), workingPath.getParent().getParent(), workingPath)
				.importInto(new NetworkSettingsDefaults().supply(new NetworkSettings()));

		Injector injector = Guice.createInjector(
				new SocialismusNetworkInjectorConfiguration(reloadableRegistry),
				new CommonConfiguration(workingPath)
		);
		Provider<NetworkSettings> settings = injector.getProvider(NetworkSettings.class);
		lifetime = settings.get().getLease();

		executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
			Thread thread = new Thread(runnable, "SocialismusNetwork");
			thread.setDaemon(true);

			return thread;
		});

		NodeId self = NodeId.of(settings.get().getNode());
		NetworkNode node = NetworkNode.builder()
				.id(self)
				.role(PlatformType.isProxy() ? NodeRole.PROXY : NodeRole.SERVER)
				.version(Constants.VERSION)
				.build();
		DefaultReplicationSystem replication = new DefaultReplicationSystem(
				self,
				Clock.systemUTC(),
				serializationService,
				executor
		);
		network = new DefaultNetworkService(replication, node, lifetime);
		ownership = new NetworkChatOwnership(network.leases(NetworkChatOwnership.LEASES), network::self, network::nodes);
		eventManager.register(new ChatRelay(
				replication,
				() -> settings.get().getChat(),
				chatCoordinationService,
				chatContainerService,
				chatHistoryService,
				network.players()
		));
		Logger.info("Joining the network as %s", self);
	}

	@Override
	public void onEnable() {
		eventManager.register(this);
		playerRegistry.addSource(network.players());
		network.players().onServerByProxy((player, server) -> executor.execute(this::learnServerName));
		chatOwnershipService.register(ownership);
		playerRegistry.getPlayers().forEach(this::host);
		resourceRegistry.get(NetworkResources.TRANSPORT).ifPresent(this::attach);

		long period = Math.max(1, lifetime.toMillis() / 3);
		executor.scheduleAtFixedRate(this::maintain, period, period, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onDisable() {
		chatOwnershipService.unregister(ownership);
		playerRegistry.removeSource(network.players());
		network.leave();
		executor.shutdown();
	}

	@Override
	public void onUnload() {
	}

	@Override
	public List<ProvidedResource<?>> provideResources() {
		return List.of(ProvidedResource.of(NetworkResources.NETWORK, network));
	}

	@SocialisticEvent
	public void onPlayerAdded(SocialismusPlayerAddedEvent event) {
		host(event.getPlayer());
	}

	// The server behind the proxy learns its name from this announcement, so it goes out as the player arrives.
	@SocialisticEvent
	public void onServerChanged(SocialismusPlayerServerChangedEvent event) {
		host(event.getPlayer());
	}

	@SocialisticEvent
	public void onPlayerRemoved(SocialismusPlayerRemovedEvent event) {
		network.players().unhost(event.getPlayer().getUniqueId());
		ownership.release(event.getPlayer());
	}

	@SocialisticEvent
	public void onResourceRegistered(ResourceRegisteredEvent event) {
		event.resource(NetworkResources.TRANSPORT).ifPresent(this::attach);
	}

	@SocialisticEvent
	public void onResourceUnregistered(ResourceUnregisteredEvent event) {
		event.resource(NetworkResources.TRANSPORT).ifPresent(network::detach);
	}

	private void host(SocialismusPlayer player) {
		if (network.self().getRole() == NodeRole.SERVER) nameServerOf(player);

		network.players().host(player);
		ownership.review(player);
	}

	private void attach(ReplicationTransport transport) {
		network.attach(transport, resourceRegistry.get(NetworkResources.STORE).orElse(null));
	}

	private void maintain() {
		try {
			learnServerName();
			network.players().renew(playerRegistry.getPlayers());
			ownership.review(playerRegistry.getPlayers());
			network.maintain();
		} catch (RuntimeException failure) {
			Logger.warn("Could not renew this node on the network: %s", failure);
		}
	}

	/**
	 * Gives a player who joins this server its name before they are judged or announced: the name it learned, or else
	 * the one the proxy announces for the player, which this server then learns.
	 */
	private void nameServerOf(SocialismusPlayer player) {
		String server = network.self().getServer();
		if (server == null) {
			server = nameByProxy(player.getUniqueId()).orElse(null);
			if (server != null) executor.execute(this::learnServerName);
		}

		if (server != null && !server.equals(player.getServer())) player.setServer(server);
	}

	/**
	 * Lets a server learn its own name from the proxy in front of it, which announces the server of each player, and
	 * gives it to the players it hosts, so a server requirement judges them here too.
	 */
	private void learnServerName() {
		NetworkNode self = network.self();
		if (self.getRole() != NodeRole.SERVER) return;

		Optional<String> learned = playerRegistry.getPlayers().stream()
				.map(player -> nameByProxy(player.getUniqueId()))
				.flatMap(Optional::stream)
				.findFirst();
		if (learned.isEmpty()) return;

		String server = learned.get();
		if (!server.equals(self.getServer())) {
			network.directory().update(self.toBuilder().server(server).build());
			Logger.info("This node is the server %s of its proxy", server);
		}
		playerRegistry.getPlayers().forEach(player -> {
			if (!server.equals(player.getServer())) player.setServer(server);
		});
	}

	/**
	 * Returns the server the proxy announces for a player of this server. A player who just came from another server
	 * may still be announced there for a moment; a name another server of the network already holds is that one's,
	 * never this server's.
	 */
	private Optional<String> nameByProxy(UUID player) {
		NodeId me = network.self().getId();
		return network.players().serverByProxy(player)
				.filter(server -> network.nodes().stream()
						.noneMatch(node -> node.getRole() == NodeRole.SERVER && !node.getId().equals(me)
								&& server.equals(node.getServer())));
	}
}
