package me.whereareiam.socialismus.module.mentionator.common.tagging;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.chat.render.ChatRenderContext;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.TagUse;
import me.whereareiam.socialismus.module.mentionator.api.model.tagging.Tagging;
import me.whereareiam.socialismus.module.mentionator.common.condition.Catalog;
import me.whereareiam.socialismus.module.mentionator.common.condition.MentionatorConditions;
import me.whereareiam.socialismus.service.chat.render.ChatMessageTransformer;
import net.kyori.adventure.text.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Shows each recipient the tags of a message in their own format, such as their own name highlighted, in place of the
 * plain format the message carries. Tags marked where the message was written are found in the order they were
 * written, so the same text written again as plain text, or past a limit, stays plain.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class TagHighlighter implements ChatMessageTransformer {
	private final MentionatorConditions conditions;

	@Override
	public Component transform(ChatRenderContext context, Component content) {
		FormattedChatMessage message = context.getMessage();
		SocialismusPlayer viewer = context.getRecipient();
		if (message == null || viewer == null || content == null) return content;

		Optional<Tagging> tagging = message.getAttribute(Tagging.ATTRIBUTE);
		if (tagging.isEmpty()) return content;

		Catalog catalog = conditions.catalog();
		Deque<TagUse> pending = new ArrayDeque<>(tagging.get().uses());

		return replace(content, pending, use -> TagRenderer.forViewer(catalog, use, viewer, message));
	}

	private static Component replace(Component node, Deque<TagUse> pending, Renderer renderer) {
		TagUse next = pending.peek();
		if (next != null && TagRenderer.marks(node, next)) {
			pending.poll();
			Component rendered = renderer.render(next);

			return rendered == null ? node : rendered;
		}
		if (node.children().isEmpty()) return node;

		List<Component> children = new ArrayList<>();
		for (Component child : node.children()) children.add(replace(child, pending, renderer));

		return node.children(children);
	}

	@FunctionalInterface
	private interface Renderer {
		Component render(TagUse use);
	}
}
