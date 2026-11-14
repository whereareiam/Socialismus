package me.whereareiam.socialismus.module.bubbler.common.worker.container;

import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerOptions;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.Bubble;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleLine;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.type.ActivatorType;
import me.whereareiam.socialismus.module.bubbler.api.type.AnimationType;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContentArrangerTest {
	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);

	@SuppressWarnings("unchecked")
	private final ContentArranger arranger = new ContentArranger(mock(WorkerProcessor.class));

	@BeforeEach
	void setUp() {
		Logger.init(mock(LoggingHelper.class));
		when(alice.getUsername()).thenReturn("Alice");

		// Serializes as Keystone does: the text is parsed as MiniMessage, with its placeholders filled in.
		SerializerEngine engine = mock(SerializerEngine.class);
		when(engine.serialize(any(Actor.class), anyString())).thenAnswer(call -> MiniMessage.miniMessage().deserialize(
				call.getArgument(1, String.class).replace("%player_name%", "Alice")
		));
		when(engine.getPlaceholderFormat()).thenReturn(SerializerOptions.PlaceholderFormat.CURLY_BRACES);
		Serializer.initialize(() -> engine);
	}

	@Test
	void showsWhatTheSenderWroteAsItCame_NeverAsFormattingOrPlaceholders() {
		BubbleMessage message = message(Component.text("<red>%player_name%</red>"));

		arranger.arrangeContent(message);

		assertEquals("> <red>%player_name%</red>", plain(lines(message).getFirst()));
	}

	@Test
	void keepsTheFormattingTheChatGaveTheMessage() {
		BubbleMessage message = message(MiniMessage.miniMessage().deserialize("<bold>loud</bold> calm"));

		arranger.arrangeContent(message);

		Component line = lines(message).getFirst();
		assertEquals("> loud calm", plain(line));
		assertEquals("<gray>> <bold>loud</bold> calm", MiniMessage.miniMessage().serialize(line).replace("</gray>", ""));
	}

	private BubbleMessage message(Component content) {
		Bubble bubble = Bubble.builder()
				.display(Bubble.Display.builder().maxLinesCount(5).maxLineWidth(30).build())
				.format(Bubble.Format.builder()
						.format("<gray>> {message}")
						.initialFormat("")
						.finalFormat("")
						.queuedFormat("")
						.separatorFormat("-")
						.build())
				.style(Bubble.Style.builder().animation(AnimationType.POPOUT).build())
				.build();

		return BubbleMessage.builder()
				.sender(alice)
				.bubble(bubble)
				.content(content)
				.activatorType(ActivatorType.CHAT)
				.build();
	}

	private static List<Component> lines(BubbleMessage message) {
		return message.getGroups().peek().getLines().stream().map(BubbleLine::getContent).toList();
	}

	private static String plain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}
}
