package me.whereareiam.socialismus.type;

/**
 * What became of a resource handed to {@link me.whereareiam.socialismus.registry.ResourceRegistry#register}.
 */
public enum ResourceRegistrationOutcome {
	/**
	 * The resource is registered under its key, and consumers were told with a
	 * {@link me.whereareiam.socialismus.event.resource.ResourceRegisteredEvent}.
	 */
	REGISTERED,

	/**
	 * Another resource is registered under the key, and stays registered; the resource was not registered. The
	 * key frees up once its resource is unregistered.
	 */
	KEY_TAKEN
}
