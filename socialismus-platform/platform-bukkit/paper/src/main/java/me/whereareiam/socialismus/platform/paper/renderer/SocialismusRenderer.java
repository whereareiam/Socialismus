package me.whereareiam.socialismus.platform.paper.renderer;

import io.papermc.paper.chat.ChatRenderer;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@RequiredArgsConstructor
public class SocialismusRenderer implements ChatRenderer {
	private final FormattedChatMessage formattedChatMessage;
	private final ChatBroadcaster chatBroadcaster;

	@Override
	public @NotNull Component render(
			@NotNull Player source,
			@NotNull Component sourceDisplayName,
			@NotNull Component message,
			@NotNull Audience viewer
	) {
		UUID viewerUuid = extractViewerUuid(viewer);

		return formattedChatMessage.getFormat()
				.replaceText(chatBroadcaster.createClearReplacement(formattedChatMessage, viewerUuid))
				.replaceText(chatBroadcaster.createMessageReplacement(formattedChatMessage.getContent()));
	}

	private UUID extractViewerUuid(Audience viewer) {
		return (viewer instanceof Player p)
				? p.getUniqueId()
				: null;
	}
}

