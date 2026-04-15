package me.whereareiam.socialismus.service.chat.render;

import me.whereareiam.socialismus.model.chat.render.ChatRenderContext;
import net.kyori.adventure.text.Component;

public interface ChatMessageTransformer {
	Component transform(ChatRenderContext context, Component message);

	default int priority() {
		return 0;
	}
}
