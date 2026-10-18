package me.whereareiam.socialismus.model.config.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Configuration class for managing messages in the Socialismus plugin.
 * This singleton class holds the global message prefix and command-specific messages.
 *
 * <p>Used for centralizing message configurations and providing consistent
 * message formatting throughout the plugin.</p>
 */
@Getter
@Setter
@ToString
public class Messages {
		/**
		 * The global prefix used for all plugin messages
		 */
		private String prefix;

		/**
		 * Configuration for command-specific messages
		 */
		private CommandMessages commands;

		/**
		 * The wording of requirements wherever no feature words them, by the requirement's key, such as
		 * {@code socialismus:world}
		 */
		private Map<String, RequirementReasons> requirements;
}
