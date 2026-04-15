package me.whereareiam.socialismus.common.chat.render;

import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.chat.render.ChatRenderContext;
import me.whereareiam.socialismus.service.chat.render.ChatMessageTransformer;
import me.whereareiam.socialismus.service.chat.render.ChatPlaceholderResolver;
import me.whereareiam.socialismus.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultChatRenderServiceTest {
	@Test
	void renderMessageAppliesTransformersInPriorityOrder() {
		FormattedChatMessage message = FormattedChatMessage.builder()
				.content(Component.text("base"))
				.format(Component.text("{message}"))
				.build();

		DefaultChatRenderService service = new DefaultChatRenderService(
				() -> Set.of(
						new SuffixTransformer("-low", -10),
						new SuffixTransformer("-high", 10)
				),
				Set::of
		);

		Component rendered = service.renderMessage(message, null);
		assertEquals("base-low-high", ComponentUtil.toPlain(rendered));
	}

	@Test
	void renderFormatUsesHighestPriorityResolver() {
		FormattedChatMessage message = FormattedChatMessage.builder()
				.format(Component.text("hello {foo} {bar} {message}"))
				.build();

		DefaultChatRenderService service = new DefaultChatRenderService(
				Set::of,
				() -> Set.of(
						new SimpleResolver("foo", Component.text("low"), 1),
						new SimpleResolver("foo", Component.text("high"), 5),
						new SimpleResolver("bar", Component.text("bar"), 0)
				)
		);

		Component rendered = service.renderFormat(message, null);
		assertEquals("hello high bar {message}", ComponentUtil.toPlain(rendered));
	}

	@Test
	void renderCombinesFormatAndTransformedMessage() {
		FormattedChatMessage message = FormattedChatMessage.builder()
				.content(Component.text("msg"))
				.format(Component.text(">> {message} <<"))
				.build();

		DefaultChatRenderService service = new DefaultChatRenderService(
				() -> Set.of(new SuffixTransformer("X", 0)),
				Set::of
		);

		Component rendered = service.render(message, null);
		assertEquals(">> msgX <<", ComponentUtil.toPlain(rendered));
	}

	private static final class SuffixTransformer implements ChatMessageTransformer {
		private final String suffix;
		private final int priority;

		private SuffixTransformer(String suffix, int priority) {
			this.suffix = suffix;
			this.priority = priority;
		}

		@Override
		public Component transform(ChatRenderContext context, Component message) {
			return Component.text(ComponentUtil.toPlain(message) + suffix);
		}

		@Override
		public int priority() {
			return priority;
		}
	}

	private static final class SimpleResolver implements ChatPlaceholderResolver {
		private final String key;
		private final Component component;
		private final int priority;

		private SimpleResolver(String key, Component component, int priority) {
			this.key = key;
			this.component = component;
			this.priority = priority;
		}

		@Override
		public String key() {
			return key;
		}

		@Override
		public Component resolve(ChatRenderContext context) {
			return component;
		}

		@Override
		public int priority() {
			return priority;
		}
	}
}
