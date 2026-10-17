package me.whereareiam.socialismus.event.plugin;

import me.whereareiam.socialismus.event.base.SynchronousEvent;

/**
 * Called by {@code /socialismus reload} once every configuration document has reloaded, whether or not some failed.
 * What is built from documents, such as the chats, the integrations or the announcers of a module, rebuilds on it.
 * It is called on the thread of the command, so the rebuild is done before the command answers.
 */
public class PluginReloadedEvent implements SynchronousEvent {
}
