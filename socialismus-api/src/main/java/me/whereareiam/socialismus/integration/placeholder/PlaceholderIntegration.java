package me.whereareiam.socialismus.integration.placeholder;

import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.SerializerIntegration;
import me.whereareiam.socialismus.type.PlaceholderResolutionMode;

import java.util.UUID;

/**
 * Integration interface for placeholder resolution systems.
 * Implementations provide the ability to resolve placeholders in text for specific players.
 * <p>
 * This interface is used for direct placeholder resolution in contexts like requirement validation,
 * while {@link SerializerIntegration} handles integration into the main serialization pipeline.
 */
public interface PlaceholderIntegration extends Integration {
	/**
	 * Resolves placeholders in the given text for a specific player.
	 *
	 * @param uniqueId The UUID of the player for placeholder context
	 * @param text     The text containing placeholders to resolve
	 * @return The text with placeholders resolved
	 */
	String resolve(UUID uniqueId, String text);

	/**
	 * Declares how this integration's placeholder output should be handled by runtime services.
	 * Defaults to a single resolution pass.
	 */
	default PlaceholderResolutionMode resolutionMode() {
		return PlaceholderResolutionMode.SINGLE_PASS;
	}
}
