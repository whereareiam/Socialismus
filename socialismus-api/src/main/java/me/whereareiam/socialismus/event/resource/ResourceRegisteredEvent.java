package me.whereareiam.socialismus.event.resource;

import me.whereareiam.socialismus.model.resource.ResourceKey;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a resource is registered, such as when the module that provides it loads. A consumer that started
 * without the resource switches over to it here, without a restart:
 * <pre>{@code
 * @SocialisticEvent
 * public void onResourceRegistered(ResourceRegisteredEvent event) {
 *     event.resource(Constants.Resources.CACHE).ifPresent(storage::useCache);
 * }
 * }</pre>
 */
public class ResourceRegisteredEvent extends ResourceEvent {
	/**
	 * @param key      the key the resource is registered under
	 * @param resource the resource
	 */
	public ResourceRegisteredEvent(@NotNull ResourceKey<?> key, @NotNull Object resource) {
		super(key, resource);
	}
}
