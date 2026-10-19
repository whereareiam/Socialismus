package me.whereareiam.socialismus.module.link;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.exception.ModuleLifecycleException;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.link.common.CommonConfiguration;
import me.whereareiam.socialismus.module.link.common.config.LinkSettings;
import me.whereareiam.socialismus.module.link.common.config.migration.LinkConfigMigrations;
import me.whereareiam.socialismus.module.link.common.transport.LinkClient;
import me.whereareiam.socialismus.module.link.common.transport.LinkHub;
import me.whereareiam.socialismus.module.network.api.NetworkResources;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.type.PlatformType;
import me.whereareiam.strata.Strata;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Carries SocialismusNetwork's messages between a proxy and its servers over a connection of their own: the proxy
 * listens, every server connects to it with the shared secret, and the proxy relays what one server sends to the
 * others. Unlike plugin messages, it needs no players online; unlike Redis, it needs nothing else running.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SocialismusLink extends SocialisticModule implements ResourceProvider {
	private final Registry<Reloadable> reloadableRegistry;

	private ReplicationTransport transport;

	@Override
	public void onLoad() {
		new Strata(List.of(LinkConfigMigrations.on(Config.configured(), workingPath))).migrate();

		Injector injector = Guice.createInjector(
				new SocialismusLinkInjectorConfiguration(reloadableRegistry),
				new CommonConfiguration(workingPath)
		);
		LinkSettings settings = injector.getInstance(LinkSettings.class);
		if (!PlatformType.isProxy()) {
			transport = new LinkClient(address(settings.getProxy()), settings.getSecret());
			return;
		}

		try {
			transport = new LinkHub(address(settings.getListen()), settings.getSecret());
		} catch (IOException failure) {
			throw new ModuleLifecycleException("Could not listen for servers at " + settings.getListen().getHost() + ":"
					+ settings.getListen().getPort() + ": " + failure.getMessage(), failure);
		}
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUnload() {
		if (transport instanceof AutoCloseable closeable) {
			try {
				closeable.close();
			} catch (Exception ignored) {
				// Closing anyway.
			}
		}
	}

	@Override
	public List<ProvidedResource<?>> provideResources() {
		return List.of(ProvidedResource.of(NetworkResources.TRANSPORT, transport));
	}

	private static InetSocketAddress address(LinkSettings.Address address) {
		return new InetSocketAddress(address.getHost(), address.getPort());
	}
}
