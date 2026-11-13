package me.whereareiam.socialismus.common.requirement.type;

import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.placeholder.PlaceholderIntegration;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.util.PlaceholderUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

/**
 * Reads a player's value of a text with placeholders, through the first placeholder plugin that is available; the
 * conditions {@code placeholder} and {@code same: { placeholder }} share it.
 */
@RequiredArgsConstructor
public final class PlaceholderReader {
	private final Provider<Set<Integration>> integrations;

	/**
	 * Resolves the placeholders of a text for a player.
	 *
	 * @param player the player
	 * @param text   the text, such as {@code %vault_eco_balance%}
	 * @return the player's value, empty without a placeholder plugin
	 */
	public @NotNull Optional<String> read(@NotNull SocialismusPlayer player, @NotNull String text) {
		return integrations.get().stream()
				.filter(PlaceholderIntegration.class::isInstance)
				.filter(Integration::isAvailable)
				.map(PlaceholderIntegration.class::cast)
				.findFirst()
				.map(integration -> PlaceholderUtil.resolve(integration, player.getUniqueId(), text));
	}
}
