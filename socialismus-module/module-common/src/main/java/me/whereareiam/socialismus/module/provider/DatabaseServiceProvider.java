package me.whereareiam.socialismus.module.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.resource.DatabaseService;
import me.whereareiam.socialismus.type.ResourceType;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class DatabaseServiceProvider implements Provider<DatabaseService> {
	private final ResourceRegistry registry;

	@Override
	public DatabaseService get() {
		Object svc = registry.get(ResourceType.CACHE)
				.orElseThrow(() -> new ProvisionException("No DatabaseService registered!"));

		if (!(svc instanceof DatabaseService))
			throw new ProvisionException("Registered object is not a DatabaseService: " + svc.getClass());

		return (DatabaseService) svc;
	}
}
