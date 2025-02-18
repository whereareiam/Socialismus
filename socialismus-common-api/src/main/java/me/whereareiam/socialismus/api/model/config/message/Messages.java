package me.whereareiam.socialismus.api.model.config.message;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@Singleton
public class Messages {
		/**
		 * The global prefix used for all plugin messages
		 */
		private String prefix;

		/**
		 * Configuration for command-specific messages
		 */
		private CommandMessages commands;
}