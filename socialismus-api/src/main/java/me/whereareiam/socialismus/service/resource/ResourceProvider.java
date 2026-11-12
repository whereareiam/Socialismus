package me.whereareiam.socialismus.service.resource;

import me.whereareiam.socialismus.model.resource.ProvidedResource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A module that provides shared resources. Socialismus registers them in the
 * {@link me.whereareiam.socialismus.registry.ResourceRegistry} right after the module is enabled, before the modules
 * that depend on it start, and unregisters them right after it is disabled. Make the resources in {@code enable},
 * where a failure stops the module; this method only hands them over:
 * <pre>{@code
 * @Override
 * public List<ProvidedResource<?>> provideResources() {
 *     return List.of(ProvidedResource.of(NetworkResources.TRANSPORT, transport));
 * }
 * }</pre>
 *
 * <p>A resource whose key another module holds already is not registered; Socialismus logs which module keeps
 * it. Keep each registered resource working until the module is disabled; the modules that depend on it stop
 * before it, and others react to its {@link me.whereareiam.socialismus.event.resource.ResourceUnregisteredEvent}.
 */
public interface ResourceProvider {
	/**
	 * Returns the resources of the module, once, right after it is enabled.
	 *
	 * @return the resources, each under its key
	 */
	@NotNull
	List<ProvidedResource<?>> provideResources();
}
