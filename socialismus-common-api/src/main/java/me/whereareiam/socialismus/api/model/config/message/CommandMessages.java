package me.whereareiam.socialismus.api.model.config.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Configuration model for command-related messages in the Socialismus plugin.
 * Contains message templates for various command responses, errors, and formatting options.
 * This class is used to load and store message configurations from the config file.
 */
@Getter
@Setter
@ToString
public class CommandMessages {
    /**
     * Message shown when a command is on cooldown.
     */
    private String cooldown;

    /**
     * Message shown when a command execution is cancelled.
     */
    private String cancelled;

    /**
     * Message shown when a player lacks permission.
     */
    private String noPermission;

    /**
     * Message shown when a command execution encounters an error.
     */
    private String executionError;

    /**
     * Message shown for invalid command syntax.
     */
    private String invalidSyntax;

    /**
     * Message shown when a boolean argument is invalid.
     */
    private String invalidSyntaxBoolean;

    /**
     * Message shown when a numeric argument is invalid.
     */
    private String invalidSyntaxNumber;

    /**
     * Message shown when a string argument is invalid.
     */
    private String invalidSyntaxString;

    /**
     * Map of custom argument-related messages.
     */
    private Map<String, String> arguments;

    /**
     * Command format configuration.
     */
    private Format format;

    /**
     * Pagination settings for multi-page command outputs.
     */
    private Pagination pagination;

    /**
     * Help command messages and format.
     */
    private HelpCommand helpCommand;

    /**
     * Debug command messages and format.
     */
    private DebugCommand debugCommand;

    /**
     * Reload command messages.
     */
    private ReloadCommand reloadCommand;

    /**
     * Clear command messages.
     */
    private ClearCommand clearCommand;

    /**
     * Configuration for command format display.
     */
    @Getter
    @Setter
    @ToString
    public static class Format {
        /**
         * Overall command format template.
         */
        private String format;

        /**
         * Format for required arguments.
         */
        private String argument;

        /**
         * Format for optional arguments.
         */
        private String optionalArgument;
    }

    /**
     * Configuration for command output pagination.
     */
    @Getter
    @Setter
    @ToString
    public static class Pagination {
        /**
         * Whether to show pagination for single pages.
         */
        private boolean showPaginationIfOnePage;

        /**
         * Pagination display format.
         */
        private String format;

        /**
         * Whether to show previous page button on first page.
         */
        private boolean showPreviousEvenIfFirst;

        /**
         * Format for previous page button.
         */
        private String previousTagFormat;

        /**
         * Whether to show next page button on last page.
         */
        private boolean showNextEvenIfLast;

        /**
         * Format for next page button.
         */
        private String nextTagFormat;
    }

    /**
     * Configuration for help command messages.
     */
    @Getter
    @Setter
    @ToString
    public static class HelpCommand {
        /**
         * Help command output format.
         */
        private List<String> format;

        /**
         * Format for individual command entries.
         */
        private String commandFormat;

        /**
         * Message shown when no commands are available.
         */
        private String noCommands;
    }

    /**
     * Configuration for debug command messages.
     */
    @Getter
    @Setter
    @ToString
    public static class DebugCommand {
        /**
         * Debug command output format.
         */
        private List<String> format;
    }

    /**
     * Configuration for reload command messages.
     */
    @Getter
    @Setter
    @ToString
    public static class ReloadCommand {
        /**
         * Message shown when reload starts.
         */
        private String reloading;

        /**
         * Message shown when reload completes.
         */
        private String reloaded;

        /**
         * Message shown when reload encounters an error.
         */
        private String exception;
    }

    /**
     * Configuration for clear command messages.
     */
    @Getter
    @Setter
    @ToString
    public static class ClearCommand {
        /**
         * Message shown when bypassing user clear restrictions.
         */
        private String bypassUser;

        /**
         * Message shown when no user history exists.
         */
        private String noUserHistory;

        /**
         * Message shown when no history exists for an ID.
         */
        private String noIdHistory;

        /**
         * Message shown when no history exists.
         */
        private String noHistory;

        /**
         * Message shown when insufficient history entries exist.
         */
        private String notEnoughHistory;

        /**
         * Message shown when history is cleared.
         */
        private String cleared;

        /**
         * Message showing amount of cleared entries.
         */
        private String clearedAmount;
    }
}
