package me.whereareiam.socialismus.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.model.CommandDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * The commands of Socialismus, {@code commands} in the data directory: how commands behave as a whole, and each
 * command's definition.
 */
@Getter
@Setter
@ToString
public class Commands {
	/**
	 * How commands behave as a whole, apart from any one definition.
	 */
	private Behavior behavior;

	/**
	 * Map of command names to their CommandDefinition configurations.
	 * Key: command name (e.g., "help", "reload")
	 * Value: CommandDefinition with aliases, permissions, descriptions, etc.
	 */
	private Map<String, CommandDefinition> commands = new HashMap<>();

	/**
	 * How commands behave as a whole.
	 */
	@Getter
	@Setter
	@ToString
	public static class Behavior {
		/**
		 * Whether commands are registered through Brigadier where the platform supports it.
		 */
		private boolean useBrigadier;

		/**
		 * Whether command suggestions are computed off the main thread where the platform supports it.
		 */
		private boolean useAsyncCompletions;

		/**
		 * How the help command lists commands.
		 */
		private Help help;

		/**
		 * When the help's page line and its arrows are shown.
		 */
		private Pagination pagination;

		/**
		 * How the help command lists commands.
		 */
		@Getter
		@Setter
		@ToString
		public static class Help {
			/**
			 * The number of commands on one page of help, at least {@code 1}.
			 */
			private int commandsPerPage;
		}

		/**
		 * When the help's page line and its arrows are shown.
		 */
		@Getter
		@Setter
		@ToString
		public static class Pagination {
			/**
			 * Whether the page line is shown when all commands fit on one page.
			 */
			private boolean showIfOnePage;

			/**
			 * Whether the previous arrow is shown on the first page, where it opens the first page again.
			 */
			private boolean showPreviousOnFirst;

			/**
			 * Whether the next arrow is shown on the last page, where it opens the last page again.
			 */
			private boolean showNextOnLast;
		}
	}
}
