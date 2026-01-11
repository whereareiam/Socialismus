package me.whereareiam.socialismus.service.chat.render;

import me.whereareiam.socialismus.model.chat.render.ChatRenderContext;
import net.kyori.adventure.text.Component;

public interface ChatPlaceholderResolver {
	String key();

	Component resolve(ChatRenderContext context);

	default int priority() {
		return 0;
	}
}
