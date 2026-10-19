package me.whereareiam.socialismus.module.network;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.resource.ResourceRegisteredEvent;
import me.whereareiam.socialismus.event.resource.ResourceUnregisteredEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.network.api.NetworkResources;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.module.network.common.CommonConfiguration;
import me.whereareiam.socialismus.module.network.common.DefaultNetworkService;
import me.whereareiam.socialismus.module.network.common.config.NetworkSettings;
import me.whereareiam.socialismus.module.network.common.config.migration.NetworkConfigMigrations;
import me.whereareiam.socialismus.module.network.common.replication.DefaultReplicationSystem;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.strata.Strata;

import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Connects this Socialismus to the others of the network: it registers the network for the modules that replicate,
 * and replicates through the transport a transport module registers, such as SocialismusRedis. Without a transport
 * this node is alone on the network.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SocialismusNetwork extends SocialisticModule implements ResourceProvider, EventListener {
	private final Registry<Reloadable> reloadableRegistry;
	private final SerializationService serializationService;
	private final ResourceRegistry resourceRegistry;
	private final EventManager eventManager;

	private ScheduledExecutorService executor;
	private DefaultNetworkService network;
	private Duration lifetime;

	@Override
	public void onLoad() {
		new Strata(List.of(NetworkConfigMigrations.on(Config.configured(), workingPath))).migrate();

		Injector injector = Guice.createInjector(
				new SocialismusNetworkInjectorConfiguration(reloadableRegistry),
				new CommonConfiguration(workingPath)
		);
		NetworkSettings settings = injector.getInstance(NetworkSettings.class);
		lifetime = settings.getLease();

		executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
			Thread thread = new Thread(runnable, "SocialismusNetwork");
			thread.setDaemon(true);
			return thread;
		});

		NodeId self = NodeId.of(settings.getNode());
		NetworkNode node = NetworkNode.builder()
				.id(self)
				.role(PlatformType.isProxy() ? NodeRole.PROXY : NodeRole.SERVER)
				.version(Constants.VERSION)
				.build();
		DefaultReplicationSystem replication = new DefaultReplicationSystem(self, Clock.systemUTC(), serializationService, executor);
		network = new DefaultNetworkService(replication, node, lifetime);
		Logger.info("Joining the network as %s", self);
	}

	@Override
	public void onEnable() {
		eventManager.register(this);
		resourceRegistry.get(NetworkResources.TRANSPORT).ifPresent(this::attach);

		long period = Math.max(1, lifetime.toMillis() / 3);
		executor.scheduleAtFixedRate(this::maintain, period, period, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onDisable() {
		if (network != null) network.leave();
		if (executor != null) executor.shutdown();
	}

	@Override
	public void onUnload() {
	}

	@Override
	public List<ProvidedResource<?>> provideResources() {
		return List.of(ProvidedResource.of(NetworkResources.NETWORK, network));
	}

	@SocialisticEvent
	public void onResourceRegistered(ResourceRegisteredEvent event) {
		event.resource(NetworkResources.TRANSPORT).ifPresent(this::attach);
	}

	@SocialisticEvent
	public void onResourceUnregistered(ResourceUnregisteredEvent event) {
		event.resource(NetworkResources.TRANSPORT).ifPresent(network::detach);
	}

	private void attach(ReplicationTransport transport) {
		network.attach(transport, resourceRegistry.get(NetworkResources.STORE).orElse(null));
	}

	private void maintain() {
		try {
			network.maintain();
		} catch (RuntimeException failure) {
			Logger.warn("Could not renew this node on the network: %s", failure);
		}
	}
}
