package me.whereareiam.socialismus.module.redis;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.redis.common.CommonConfiguration;
import me.whereareiam.socialismus.module.redis.common.network.RedisNetworkResources;
import me.whereareiam.socialismus.module.redis.common.config.migration.RedisConfigMigrations;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.resource.CacheService;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.strata.Strata;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SocialismusRedis extends SocialisticModule implements ResourceProvider {
	private final Registry<Reloadable> reloadableRegistry;
	private final EventManager eventManager;
	private static final String NETWORK_API = "me.whereareiam.socialismus.module.network.api.NetworkResources";

	private Injector injector;
	private RedisNetworkResources network;

	@Override
	public void onLoad() {
		new Strata(List.of(RedisConfigMigrations.on(Config.configured(), workingPath))).migrate();

		injector =
				Guice.createInjector(
						new SocialismusRedisInjectorConfiguration(
								reloadableRegistry,
								eventManager
						),
						new CommonConfiguration(workingPath));
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUnload() {
		if (network != null) network.close();
	}

	@Override
	public List<ProvidedResource<?>> provideResources() {
		List<ProvidedResource<?>> resources = new ArrayList<>(List.of(
				ProvidedResource.of(Constants.Resources.SYNC, injector.getInstance(SyncService.class)),
				ProvidedResource.of(Constants.Resources.CACHE, injector.getInstance(CacheService.class))
		));
		if (isNetworkInstalled()) {
			network = new RedisNetworkResources(injector.getInstance(JedisPool.class));
			resources.addAll(network.provide());
		}

		return resources;
	}

	/**
	 * Returns whether SocialismusNetwork is installed, whose API this module reaches only then.
	 */
	private boolean isNetworkInstalled() {
		try {
			Class.forName(NETWORK_API, false, getClass().getClassLoader());
			return true;
		} catch (ClassNotFoundException absent) {
			return false;
		}
	}
}
