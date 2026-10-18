package me.whereareiam.socialismus.module.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.resource.DatabaseService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class DatabaseServiceProvider implements Provider<DatabaseService> {
	private final ResourceRegistry registry;

	@Override
	public DatabaseService get() {
		return registry.get(Constants.Resources.DATABASE)
				.orElseThrow(() -> new ProvisionException("No DatabaseService registered!"));
	}
}
