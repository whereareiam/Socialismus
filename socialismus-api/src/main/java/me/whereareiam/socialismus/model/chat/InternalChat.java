package me.whereareiam.socialismus.model.chat;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


/**
 * Represents an internal chat configuration with additional server-side properties.
 * Extends the base {@link Chat} class to add functionality specific to the plugin's
 * internal chat handling system.
 * <p>
 * This class adds vanilla message sending control and provides a factory method
 * to create internal chat instances from regular chat configurations.
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class InternalChat extends Chat {
	/**
	 * Determines if messages should be sent through vanilla chat system
	 */
	private boolean vanillaSending;

	/**
	 * Creates an internal chat instance from a base chat configuration.
	 *
	 * @param chat the base chat configuration
	 * @return a new internal chat instance with vanilla sending enabled
	 */
	public static InternalChat from(Chat chat) {
		return InternalChat.builder()
				.id(chat.getId())
				.priority(chat.getPriority())
				.enabled(chat.isEnabled())
				.triggers(chat.getTriggers())
				.formats(chat.getFormats())
				.requirements(chat.getRequirements())
				.vanillaSending(true)
				.build();
	}

	/**
	 * Compares this internal chat with another object.
	 * Two internal chats are considered equal if they have the same symbol,
	 * vanilla sending setting, and ID.
	 *
	 * @param obj the object to compare with
	 * @return true if the objects are equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InternalChat chat) {
			return chat.isVanillaSending() == this.isVanillaSending()
					&& chat.getId().equals(this.getId());
		}

		return false;
	}
}