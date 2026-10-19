package me.whereareiam.socialismus.event.resource;

import me.whereareiam.socialismus.model.resource.ResourceKey;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a resource is unregistered, such as when the module that provides it unloads. The registry no
 * longer holds it, but the resource still works until the event returns, so a consumer that used it switches
 * back here, for example from the network to its own memory:
 * <pre>{@code
 * @SocialisticEvent
 * public void onResourceUnregistered(ResourceUnregisteredEvent event) {
 *     event.resource(NetworkResources.NETWORK).ifPresent(network -> storage.useMemory());
 * }
 * }</pre>
 */
public class ResourceUnregisteredEvent extends ResourceEvent {
	/**
	 * @param key      the key the resource was registered under
	 * @param resource the resource
	 */
	public ResourceUnregisteredEvent(@NotNull ResourceKey<?> key, @NotNull Object resource) {
		super(key, resource);
	}
}
