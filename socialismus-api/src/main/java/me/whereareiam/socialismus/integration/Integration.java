package me.whereareiam.socialismus.integration;

/**
 * Base interface for all plugin integrations in the Socialismus plugin.
 * Defines the core methods that all integrations must implement to provide
 * identification and availability status.
 *
 * This interface serves as a foundation for specific integration types
 * such as formatting, placeholders, and other external plugin integrations.
 */
public interface Integration {
    /**
     * Gets the name of the integration.
     *
     * @return the integration's name
     */
    String getName();

    /**
     * Checks if the integration is available for use.
     * This typically verifies if the required plugin or service is present.
     *
     * @return true if the integration is available, false otherwise
     */
    boolean isAvailable();
}