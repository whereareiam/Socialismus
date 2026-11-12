package me.whereareiam.socialismus.command.help;

import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.model.SerializerOptions;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.config.message.HelpMessages;
import me.whereareiam.socialismus.model.config.message.PaginationMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HelpRendererTest {
	private static final String LINE = "socialismus.help.pagination.line";
	private static final String NEXT = "socialismus.help.pagination.next";

	private final Actor reader = mock(Actor.class);

	private HelpRenderer renderer;

	@BeforeEach
	void setUp() {
		SerializerEngine engine = mock(SerializerEngine.class);
		when(engine.serialize(any(SerializerContent.class))).thenAnswer(call -> parse(call.getArgument(0)));
		when(engine.getPlaceholderFormat()).thenReturn(SerializerOptions.PlaceholderFormat.CURLY_BRACES);
		Serializer.initialize(() -> engine);

		HelpMessages help = new HelpMessages();
		help.setTitle("<gold>{host} help");

		PaginationMessages pagination = new PaginationMessages();
		pagination.setLine("{previous}[{current}/{max}]{next}");
		pagination.setPrevious("<red>« ");
		pagination.setNext(" <green>{page} »");

		renderer = HelpRenderer.of(help, pagination);
	}

	@Test
	void writesTextArgumentsIntoTheirPlaceholders() {
		Component title = Component.translatable(
				"socialismus.help.title",
				HelpRenderer.ARGUMENTS.create("host", "Socialismus")
		);

		assertEquals("Socialismus help", plain(renderer.render(reader, title)));
	}

	@Test
	void putsRenderedArrowsWithTheirClickIntoTheLine() {
		ClickEvent click = ClickEvent.runCommand("/socialismus help 2");
		Component next = Component.translatable(NEXT, HelpRenderer.ARGUMENTS.create("page", 2)).clickEvent(click);
		Component line = Component.translatable(
				LINE,
				HelpRenderer.ARGUMENTS.create("current", 1),
				HelpRenderer.ARGUMENTS.create("max", 2),
				HelpRenderer.ARGUMENTS.create("previous", Component.empty()),
				HelpRenderer.ARGUMENTS.create("next", next)
		);

		Component rendered = renderer.render(reader, line);

		assertEquals("[1/2] 2 »", plain(rendered));
		assertEquals(click, findClick(rendered));
	}

	@Test
	void rendersTextsInsideOtherComponents() {
		Component lines = Component.text("")
				.append(Component.text("before "))
				.append(Component.translatable("socialismus.help.title", HelpRenderer.ARGUMENTS.create("host", "Socialismus")));

		assertEquals("before Socialismus help", plain(renderer.render(reader, lines)));
	}

	@Test
	void keepsTranslatablesItHasNoTextFor() {
		Component other = Component.translatable("other.key");

		assertEquals(other, renderer.render(reader, other));
	}

	private static Component parse(SerializerContent content) {
		String message = content.getMessage();
		for (Map.Entry<String, String> placeholder : content.getPlaceholders().entrySet())
			message = message.replace("{" + placeholder.getKey() + "}", placeholder.getValue());

		return MiniMessage.miniMessage().deserialize(message);
	}

	private static String plain(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

	private static ClickEvent findClick(Component component) {
		if (component.clickEvent() != null) return component.clickEvent();

		return component.children().stream()
				.map(HelpRendererTest::findClick)
				.filter(click -> click != null)
				.findFirst()
				.orElse(null);
	}
}
