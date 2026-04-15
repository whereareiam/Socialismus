package me.whereareiam.socialismus.service.chat.render;

import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import net.kyori.adventure.text.Component;

public interface ChatRenderService {
	Component render(FormattedChatMessage message, SocialismusPlayer recipient);

	Component renderMessage(FormattedChatMessage message, SocialismusPlayer recipient);

	Component renderFormat(FormattedChatMessage message, SocialismusPlayer recipient);
}
