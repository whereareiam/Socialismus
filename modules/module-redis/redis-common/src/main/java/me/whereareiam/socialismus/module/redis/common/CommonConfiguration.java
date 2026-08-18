package me.whereareiam.socialismus.module.redis.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.redis.api.model.RedisSettings;
import me.whereareiam.socialismus.module.redis.common.config.provider.RedisSettingsProvider;
import me.whereareiam.socialismus.module.redis.common.provider.JedisPoolProvider;
import me.whereareiam.socialismus.module.redis.common.service.RedisCacheService;
import me.whereareiam.socialismus.module.redis.common.service.RedisSyncService;
import me.whereareiam.socialismus.service.resource.CacheService;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class CommonConfiguration extends AbstractModule {
	private final Path workingPath;

	@Override
	protected void configure() {
		requestInjection(this);

		// Configuration
		bind(RedisSettingsProvider.class).asEagerSingleton();
		bind(RedisSettings.class).toProvider(RedisSettingsProvider.class);

		// General
		bind(JedisPool.class).toProvider(JedisPoolProvider.class);
		bind(SyncService.class).to(RedisSyncService.class);
		bind(CacheService.class).to(RedisCacheService.class);
	}

	@Provides
	@Singleton
	@Named("workingPath")
	Path provideWorkingPath() {
		return ensureDirectory(workingPath, "workingPath");
	}

	private Path ensureDirectory(Path path, String label) {
		try {
			Files.createDirectories(path);
			return path;
		} catch (IOException e) {
			throw new RuntimeException("Failed to create " + label + " directory", e);
		}
	}
}
