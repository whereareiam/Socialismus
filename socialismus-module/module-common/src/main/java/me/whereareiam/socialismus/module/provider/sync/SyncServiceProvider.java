package me.whereareiam.socialismus.module.provider.sync;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.config.Sync;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.socialismus.type.ResourceType;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SyncServiceProvider implements Provider<SyncService> {
	private final ResourceRegistry registry;
	private final Provider<Sync> syncSettings;

	@Override
	public SyncService get() {
		if (!syncSettings.get().isEnabled())
			return new DummySyncService();

		Object svc = registry.get(ResourceType.SYNC)
				.orElseThrow(() -> new ProvisionException("No SyncService registered!"));

		if (!(svc instanceof SyncService)) {
			throw new ProvisionException("Registered object is not a SyncService: " + svc.getClass());
		}

		return (SyncService) svc;
	}
}
