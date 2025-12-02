package me.whereareiam.socialismus.event.plugin;

import me.whereareiam.socialismus.event.base.SynchronousEvent;

/**
 * Fired after the platform-specific bootstrap phase has completed.
 * <p>
 * At this point dependency resolution and injector setup should be finished,
 * but the plugin has not yet been fully enabled.
 */
public class PluginBootstrappedEvent implements SynchronousEvent {
}


