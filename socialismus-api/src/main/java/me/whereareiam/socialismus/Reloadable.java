package me.whereareiam.socialismus;

/**
 * A configuration document that {@code /socialismus reload} reads again. Only documents are reloadable: what is built
 * from them, such as the chats, rebuilds on {@link me.whereareiam.socialismus.event.plugin.PluginReloadedEvent},
 * once every document has reloaded.
 */
public interface Reloadable {
	/**
	 * Reads the document again. A failure keeps what the document held before.
	 */
	void reload();
}
