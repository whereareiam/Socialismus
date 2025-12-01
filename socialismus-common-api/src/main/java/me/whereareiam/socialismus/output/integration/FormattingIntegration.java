package me.whereareiam.socialismus.output.integration;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;

/**
 * Interface for integrating external formatting systems into the Socialismus plugin.
 * Extends the base {@link Integration} interface to provide text formatting capabilities
 * that can be applied to player-specific content.
 * <p>
 * This interface allows different formatting implementations to be used interchangeably
 * for processing text content with player-specific formatting.
 */
public interface FormattingIntegration extends Integration {
		/**
		 * Formats the given content string for a specific player.
		 *
		 * @param socialismusPlayer the player for whom the content is being formatted
		 * @param content the text content to format
		 * @return the formatted string with applied formatting
		 */
		String format(SocialismusPlayer socialismusPlayer, String content);
}