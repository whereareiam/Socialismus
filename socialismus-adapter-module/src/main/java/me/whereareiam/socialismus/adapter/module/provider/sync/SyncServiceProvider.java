package me.whereareiam.socialismus.adapter.module.provider.sync;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.output.resource.ResourceRegistry;
import me.whereareiam.socialismus.output.resource.sync.SyncService;
import me.whereareiam.socialismus.type.ResourceType;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SyncServiceProvider implements Provider<SyncService> {
	private final ResourceRegistry registry;
	private final Provider<Settings> settings;

	@Override
	public SyncService get() {
		if (!settings.get().getSynchronization().isEnabled())
			return new DummySyncService();

		Object svc = registry.get(ResourceType.SYNC)
				.orElseThrow(() -> new ProvisionException("No SyncService registered!"));

		if (!(svc instanceof SyncService)) {
			throw new ProvisionException("Registered object is not a SyncService: " + svc.getClass());
		}

		return (SyncService) svc;
	}
}
