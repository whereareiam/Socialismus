package me.whereareiam.socialismus.integration.placeholder;

import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.MessageDecorator;
import me.whereareiam.socialismus.util.PlaceholderUtil;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class PlaceholderMessageDecorator implements MessageDecorator {
	private final PlaceholderIntegration integration;

	@Override
	@NotNull
	public SerializerContent decorate(@NotNull SerializerContent content) {
		Actor receiver = content.getReceiver();
		if (receiver == null) return content;

		content.setMessage(PlaceholderUtil.resolve(
				integration,
				receiver.getUniqueId(),
				content.getMessage()
		));
		return content;
	}

	@Override
	public boolean isAvailable() {
		return integration.isAvailable();
	}
}
