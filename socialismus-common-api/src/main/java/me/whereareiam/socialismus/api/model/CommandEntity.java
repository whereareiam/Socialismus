package me.whereareiam.socialismus.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Configuration model for command entities in the Socialismus plugin.
 * Defines command properties such as enabled state, aliases, permissions,
 * and cooldown settings loaded from the configuration file.
 *
 * <p>This class uses the builder pattern for flexible instantiation
 * and provides easy serialization to/from configuration format.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class CommandEntity {
    /**
     * Whether the command is enabled.
     */
    private boolean enabled;

    /**
     * List of command aliases/names that trigger this command.
     */
    private List<String> aliases;

    /**
     * Permission required to execute this command.
     */
    private String permission;

    /**
     * Description of what the command does.
     */
    private String description;

    /**
     * Usage syntax for the command.
     */
    private String usage;

    /**
     * Cooldown settings for the command.
     */
    private Cooldown cooldown;

    /**
     * Nested configuration class for command cooldown settings.
     * Defines cooldown behavior and restrictions for command execution.
     */
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class Cooldown {
        /**
         * Whether cooldown is enabled for this command.
         */
        private boolean enabled;

        /**
         * Duration of the cooldown in seconds.
         */
        private int duration;

        /**
         * Cooldown group identifier for shared cooldowns.
         */
        private String group;
    }
}