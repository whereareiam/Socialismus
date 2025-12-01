package me.whereareiam.socialismus.input.event.plugin;

import me.whereareiam.socialismus.input.event.base.Event;

/**
 * Event that is fired when the Socialismus plugin has completed its initialization phase.
 * This event indicates that all core systems, configurations, and services have been
 * successfully loaded and the plugin is ready for operation.
 *
 * <p>This event can be used by addon developers to initialize their own components
 * or perform actions that require the plugin to be fully operational.</p>
 */
public class PluginInitializedEvent implements Event {
}