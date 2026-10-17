package me.whereareiam.socialismus.module.provider.cache;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.resource.CacheService;
import me.whereareiam.socialismus.type.ResourceType;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CacheServiceProvider implements Provider<CacheService> {
	private final ResourceRegistry registry;
	private final Scheduler scheduler;

	@Override
	public CacheService get() {
		Object svc = registry.get(ResourceType.CACHE)
				.orElseGet(() -> {
					LocalCacheService localCache = new LocalCacheService(scheduler);
					localCache.initialize();
					return localCache;
				});

		if (!(svc instanceof CacheService)) {
			throw new ProvisionException("Registered object is not a CacheService: " + svc.getClass());
		}

		return (CacheService) svc;
	}
}
