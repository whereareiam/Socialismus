package me.whereareiam.socialismus.adapter.module.provider.cache;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.output.resource.CacheService;
import me.whereareiam.socialismus.api.output.resource.ResourceRegistry;
import me.whereareiam.socialismus.api.type.ResourceType;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CacheServiceProvider implements Provider<CacheService> {
	private final ResourceRegistry registry;

	@Override
	public CacheService get() {
		Object svc = registry.get(ResourceType.CACHE)
				.orElse(new DummyCacheService());

		if (!(svc instanceof CacheService)) {
			throw new ProvisionException("Registered object is not a CacheService: " + svc.getClass());
		}

		return (CacheService) svc;
	}
}
