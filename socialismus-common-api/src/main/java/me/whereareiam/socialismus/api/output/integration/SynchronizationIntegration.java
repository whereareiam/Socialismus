package me.whereareiam.socialismus.api.output.integration;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface for handling cross-server synchronization functionality.
 * Extends the base {@link Integration} interface to provide methods for
 * synchronizing messages and player locations across multiple servers.
 *
 * <p>This integration is used to:</p>
 * <ul>
 *   <li>Synchronize chat messages between servers</li>
 *   <li>Track player locations across server instances</li>
 * </ul>
 */
public interface SynchronizationIntegration extends Integration {
    /**
     * Synchronizes content across servers in a specific channel.
     *
     * @param channel the channel to synchronize content in
     * @param content the content to be synchronized
     */
    void sync(String channel, String content);

    /**
     * Gets the current server location of a player.
     *
     * @param uniqueId the UUID of the player
     * @return Optional containing the server name, or empty if not found
     */
    Optional<String> getLocation(UUID uniqueId);
}