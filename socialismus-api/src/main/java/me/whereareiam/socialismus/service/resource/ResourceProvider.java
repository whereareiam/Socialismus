package me.whereareiam.socialismus.service.resource;

import me.whereareiam.socialismus.model.resource.ProvidedResource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A module that provides shared resources. Socialismus registers them in the
 * {@link me.whereareiam.socialismus.registry.ResourceRegistry} after the module's {@code onLoad}, and
 * unregisters them when the module unloads, before its {@code onUnload}:
 * <pre>{@code
 * @Override
 * public List<ProvidedResource<?>> provideResources() {
 *     return List.of(
 *             ProvidedResource.of(Constants.Resources.SYNC, injector.getInstance(SyncService.class)),
 *             ProvidedResource.of(Constants.Resources.CACHE, injector.getInstance(CacheService.class))
 *     );
 * }
 * }</pre>
 *
 * <p>A resource whose key another module holds already is not registered; Socialismus logs which module keeps
 * it. Keep each registered resource working until {@code onUnload}, so that consumers can still use it while
 * they react to its {@link me.whereareiam.socialismus.event.resource.ResourceUnregisteredEvent}.
 */
public interface ResourceProvider {
	/**
	 * Returns the resources of the module, once, after its {@code onLoad}.
	 *
	 * @return the resources, each under its key
	 */
	@NotNull
	List<ProvidedResource<?>> provideResources();
}
