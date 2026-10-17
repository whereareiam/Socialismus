package me.whereareiam.socialismus.lifecycle;

/**
 * The phases Socialismus starts in, in the order they run. Every {@link RuntimeLifecycle} names the phase it belongs
 * to; Socialismus starts all lifecycles of a phase before the first one of the next phase, and stops them phase by
 * phase in reverse.
 */
public enum StartupPhase {
	/** The data directory is migrated to the current layout and the configuration documents are loaded. */
	CONFIGURATION,
	/** The log of Socialismus is connected to the server console. */
	LOGGING,
	/** The integrations with other plugins on the server start, such as PlaceholderAPI and PacketEvents. */
	INTEGRATIONS,
	/** The core starts: the chats, the chat workers and the commands. */
	RUNTIME,
	/** The modules are loaded and enabled. */
	MODULES,
	/** Socialismus starts handling players: listeners, synchronization, the welcome banner and update checks. */
	CONNECTIONS
}
