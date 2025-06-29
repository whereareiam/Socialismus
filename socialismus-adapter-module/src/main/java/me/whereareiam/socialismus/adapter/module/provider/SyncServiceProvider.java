package me.whereareiam.socialismus.adapter.module.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.output.resource.ResourceRegistry;
import me.whereareiam.socialismus.api.output.resource.sync.SyncService;
import me.whereareiam.socialismus.api.type.ResourceType;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SyncServiceProvider implements Provider<SyncService> {
	private final ResourceRegistry registry;

	@Override
	public SyncService get() {
		Object svc = registry.get(ResourceType.SYNC)
				.orElseThrow(() -> new ProvisionException("No SyncService registered!"));
        
		if (!(svc instanceof SyncService))
			throw new ProvisionException("Registered object is not a SyncService: " + svc.getClass());

		return (SyncService) svc;
	}
}
