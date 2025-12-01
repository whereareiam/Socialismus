package me.whereareiam.socialismus.output.listener;

/**
 * Interface for managing event listener registration in the Socialismus plugin.
 * Provides methods to register dynamic event listener.
 * <p>
 * This interface handles the registration of event listeners that can respond
 * to various plugin and server events.
 */
public interface ListenerRegistrar {
	/**
	 * Registers all static listeners configured for the plugin.
	 * This method should be called during plugin initialization.
	 */
	void registerListeners();

	/**
	 * Registers a dynamic listener for a specific event type.
	 *
	 * @param eventClass the class of the event to listen for
	 * @param listener   the dynamic listener implementation
	 * @param <T>        the type of event
	 */
	<T> void registerListener(Class<T> eventClass, DynamicListener<T> listener);
}