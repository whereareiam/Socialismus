package me.whereareiam.socialismus.command.help;

import me.whereareiam.commandant.builder.ArgumentFactory;
import me.whereareiam.commandant.builder.HelpBuilder;
import me.whereareiam.commandant.builder.PaginationBuilder;
import me.whereareiam.keystone.Actor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.config.message.HelpMessages;
import me.whereareiam.socialismus.model.config.message.PaginationMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.VirtualComponent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders the help Commandant builds for one reader: each of its translatable components becomes the text
 * {@code messages} holds for its key, with the named arguments filled into the placeholders of their names.
 *
 * <pre>{@code
 * HelpRenderer renderer = HelpRenderer.of(messages.getCommands().getHelp(), messages.getCommands().getPagination());
 * sender.sendMessage(renderer.render(sender, help.build(commands, page)));
 * }</pre>
 */
public final class HelpRenderer {
	/**
	 * The prefix of the help's keys.
	 */
	public static final String PREFIX = "socialismus.help";

	/**
	 * Makes the named arguments of the help's texts, for Commandant's help and pagination builders.
	 */
	public static final ArgumentFactory ARGUMENTS = HelpArgument::create;

	private static final String PAGINATION_PREFIX = PREFIX + '.' + HelpBuilder.PAGINATION;

	private final Map<String, String> texts;

	private HelpRenderer(@NotNull Map<String, String> texts) {
		this.texts = texts;
	}

	/**
	 * Creates a renderer of the help's texts.
	 *
	 * @param help       the texts of the help
	 * @param pagination the texts of the page line
	 * @return the renderer
	 */
	@NotNull
	public static HelpRenderer of(@NotNull HelpMessages help, @NotNull PaginationMessages pagination) {
		Map<String, String> texts = new HashMap<>();
		texts.put(HelpBuilder.Text.TITLE.under(PREFIX), help.getTitle());
		texts.put(HelpBuilder.Text.COMMAND.under(PREFIX), help.getCommand());
		texts.put(HelpBuilder.Text.ARGUMENT.under(PREFIX), help.getArgument());
		texts.put(HelpBuilder.Text.OPTIONAL_ARGUMENT.under(PREFIX), help.getOptionalArgument());
		texts.put(HelpBuilder.Text.EMPTY.under(PREFIX), help.getEmpty());

		texts.put(PaginationBuilder.Text.LINE.under(PAGINATION_PREFIX), pagination.getLine());
		texts.put(PaginationBuilder.Text.PREVIOUS.under(PAGINATION_PREFIX), pagination.getPrevious());
		texts.put(PaginationBuilder.Text.NEXT.under(PAGINATION_PREFIX), pagination.getNext());

		return new HelpRenderer(texts);
	}

	/**
	 * Renders a help, or part of one, for a reader. Components that are not texts of the help keep their content
	 * and have their children rendered.
	 *
	 * @param reader    the reader, whose placeholders the texts take
	 * @param component the help
	 * @return the rendered help
	 */
	@NotNull
	public Component render(@NotNull Actor reader, @NotNull Component component) {
		if (component instanceof TranslatableComponent translatable && texts.get(translatable.key()) != null)
			return translate(reader, translatable, texts.get(translatable.key()));

		return component.children(component.children().stream()
				.map(child -> render(reader, child))
				.toList());
	}

	@NotNull
	private Component translate(@NotNull Actor reader, @NotNull TranslatableComponent component, @NotNull String text) {
		Map<String, String> written = new HashMap<>();
		Map<String, Component> shown = new HashMap<>();
		for (TranslationArgument argument : component.arguments()) {
			if (!(argument.asComponent() instanceof VirtualComponent virtual)) continue;

			switch (virtual.renderer()) {
				case HelpArgument.Written value -> written.put(value.name(), value.value());
				case HelpArgument.Shown value -> shown.put(value.name(), render(reader, value.value()));
				default -> {
				}
			}
		}

		Component rendered = Serializer.serialize(SerializerContent.builder()
				.receiver(reader)
				.message(text)
				.placeholders(written)
				.build());
		for (Map.Entry<String, Component> entry : shown.entrySet())
			rendered = rendered.replaceText(replacement -> replacement
					.matchLiteral(Serializer.placeholder(entry.getKey()))
					.replacement(entry.getValue()));

		return Component.text()
				.style(component.style())
				.append(rendered)
				.build();
	}
}
