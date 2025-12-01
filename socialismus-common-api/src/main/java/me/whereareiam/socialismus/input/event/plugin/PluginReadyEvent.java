package me.whereareiam.socialismus.input.event.plugin;

import me.whereareiam.socialismus.input.event.base.SynchronousEvent;

/**
 * Fired when the Socialismus plugin is ready for normal operation.
 * <p>
 * Core systems should be initialized and platform integrations registered
 * when this event is emitted.
 */
public class PluginReadyEvent implements SynchronousEvent {
}


