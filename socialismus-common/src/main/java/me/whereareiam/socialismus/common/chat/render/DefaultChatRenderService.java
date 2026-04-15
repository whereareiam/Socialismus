package me.whereareiam.socialismus.common.chat.render;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.chat.render.ChatRenderContext;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.chat.render.ChatRenderService;
import me.whereareiam.socialismus.service.chat.render.ChatMessageTransformer;
import me.whereareiam.socialismus.service.chat.render.ChatPlaceholderResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class DefaultChatRenderService implements ChatRenderService {
	private final Provider<Set<ChatMessageTransformer>> transformersProvider;
	private final Provider<Set<ChatPlaceholderResolver>> resolversProvider;

	@Override
	public Component render(FormattedChatMessage message, SocialismusPlayer recipient) {
		Component format = renderFormat(message, recipient);
		Component messageComponent = renderMessage(message, recipient);
		return replacePlaceholder(format, "message", messageComponent);
	}

	@Override
	public Component renderMessage(FormattedChatMessage message, SocialismusPlayer recipient) {
		Component content = message != null && message.getContent() != null
				? message.getContent()
				: Component.empty();
		if (message == null) {
			return content;
		}

		ChatRenderContext context = new ChatRenderContext(message, recipient);
		List<ChatMessageTransformer> transformers = new ArrayList<>(transformersProvider.get());
		transformers.sort(Comparator
				.comparingInt(ChatMessageTransformer::priority)
				.thenComparing(t -> t.getClass().getName()));

		for (ChatMessageTransformer transformer : transformers) {
			Component transformed = transformer.transform(context, content);
			if (transformed != null) {
				content = transformed;
			}
		}

		return content;
	}

	@Override
	public Component renderFormat(FormattedChatMessage message, SocialismusPlayer recipient) {
		Component format = message != null && message.getFormat() != null
				? message.getFormat()
				: Component.empty();
		if (message == null) {
			return format;
		}

		ChatRenderContext context = new ChatRenderContext(message, recipient);
		Map<String, ChatPlaceholderResolver> resolvers = selectResolvers(resolversProvider.get());

		List<String> keys = new ArrayList<>(resolvers.keySet());
		keys.sort(String::compareTo);

		for (String key : keys) {
			if ("message".equals(key)) continue;
			ChatPlaceholderResolver resolver = resolvers.get(key);
			if (resolver == null) continue;
			Component replacement = resolver.resolve(context);
			if (replacement == null) continue;
			format = replacePlaceholder(format, key, replacement);
		}

		return format;
	}

	private Map<String, ChatPlaceholderResolver> selectResolvers(Set<ChatPlaceholderResolver> resolvers) {
		Map<String, ChatPlaceholderResolver> selected = new HashMap<>();
		if (resolvers == null) return selected;

		for (ChatPlaceholderResolver resolver : resolvers) {
			if (resolver == null) continue;
			String key = resolver.key();
			if (key == null || key.isBlank()) continue;

			ChatPlaceholderResolver existing = selected.get(key);
			if (existing == null) {
				selected.put(key, resolver);
				continue;
			}

			int priority = resolver.priority();
			int existingPriority = existing.priority();
			if (priority > existingPriority) {
				selected.put(key, resolver);
			} else if (priority == existingPriority
					&& resolver.getClass().getName().compareTo(existing.getClass().getName()) < 0) {
				selected.put(key, resolver);
			}
		}

		return selected;
	}

	private Component replacePlaceholder(Component base, String key, Component replacement) {
		if (base == null) return Component.empty();
		if (key == null || key.isBlank()) return base;
		if (replacement == null) return base;

		return base.replaceText(TextReplacementConfig.builder()
				.matchLiteral("{" + key + "}")
				.replacement(replacement)
				.build());
	}
}
