package me.whereareiam.socialismus.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.type.EventPriority;

/**
 * Configuration model for dynamic event listeners in the Socialismus plugin.
 * This class defines the registration and priority settings for event handlers
 * that can be configured through the plugin's configuration files.
 *
 * Properties:
 * - register: Determines if the event listener should be registered
 * - priority: Defines the execution priority that maps to platform-specific priorities
 */
@Getter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Event {
    /**
     * Whether this event listener should be registered
     */
    private boolean register;

    /**
     * The priority level for this event listener
     */
    private EventPriority priority;
}