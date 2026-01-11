package me.whereareiam.socialismus.platform.paper.renderer;

import io.papermc.paper.chat.ChatRenderer;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.chat.render.ChatRenderService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


@RequiredArgsConstructor
public class SocialismusRenderer implements ChatRenderer {
	private final FormattedChatMessage formattedChatMessage;
	private final ChatRenderService chatRenderService;
	private final PlayerRegistry playerRegistry;

	@Override
	public @NotNull Component render(
			@NotNull Player source,
			@NotNull Component sourceDisplayName,
			@NotNull Component message,
			@NotNull Audience viewer
	) {
		SocialismusPlayer recipient = extractRecipient(viewer);
		return chatRenderService.render(formattedChatMessage, recipient);
	}

	private SocialismusPlayer extractRecipient(Audience viewer) {
		if (!(viewer instanceof Player player)) {
			return null;
		}
		return playerRegistry.getPlayerData(player.getUniqueId()).orElse(null);
	}
}
