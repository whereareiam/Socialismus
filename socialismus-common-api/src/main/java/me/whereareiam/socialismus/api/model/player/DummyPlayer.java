package me.whereareiam.socialismus.api.model.player;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.position.Position;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.Locale;
import java.util.UUID;

/**
 * Represents a lightweight player entity in the Socialismus plugin.
 * This class provides essential player properties and functionality without
 * being tied to a specific platform implementation.
 */
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class DummyPlayer {
	/**
	 * The player's username
	 */
	private final String username;

	/**
	 * The player's unique identifier
	 */
	private final UUID uniqueId;

	/**
	 * The player's current server (null if not proxy or sync disabled).
	 * Can be a server name or an identifier, if synchronization is enabled
	 */
	@Setter
	private String server;

	/**
	 * The player's current location (world name or null)
	 */
	@Setter
	private String location;

	/**
	 * The player's preferred locale
	 */
	@Setter
	private Locale locale;

	/**
	 * The last chat instance the player interacted with
	 */
	@Setter
	private Chat lastChat;

	/**
	 * The player's message receiver interface
	 */
	private final transient Audience audience;

	/**
	 * The platform interactor for interacting with the player
	 */
	@Setter
	private transient PlatformInteractor interactor;

	/**
	 * Sends a message to the player using the Adventure API
	 *
	 * @param component the message to send
	 */
	public void sendMessage(Component component) {
		audience.sendMessage(component);
	}

	/**
	 * Checks if the player has a specific permission
	 *
	 * @param permission the permission to check
	 * @return true if the player has the permission, false otherwise
	 */
	public boolean hasPermission(String permission) {
		return interactor.hasPermission(this, permission);
	}

	/**
	 * Gets the position of the player.
	 *
	 * @return the position of the player
	 */
	public Position getPosition() {
		return interactor.getPosition(this);
	}

	/**
	 * Gets the eye position of the player.
	 *
	 * @return the eye position of the player
	 */
	public Position getEyePosition() {
		return interactor.getEyePosition(this);
	}
}