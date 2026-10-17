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
	}
}
