package me.whereareiam.socialismus.model.config.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.commandant.model.message.ExceptionMessages;
import me.whereareiam.commandant.model.message.HelpMessages;
import me.whereareiam.commandant.model.message.PaginationMessages;

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
	 * Exception messages from Commandant.
	 */
	private ExceptionMessages exceptions;

	/**
	 * Map of custom argument-related messages.
	 */
	private Map<String, String> arguments;

	/**
	 * Command format configuration.
	 */
	private Format format;

	/**
	 * Pagination configuration from Commandant.
	 */
	private PaginationMessages pagination;

	/**
	 * Help command formatting configuration from Commandant.
	 */
	private HelpMessages help;

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
		private String moduleFormat;
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
