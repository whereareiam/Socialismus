package me.whereareiam.socialismus.command.help;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.VirtualComponentRenderer;
import org.jetbrains.annotations.NotNull;

/**
 * A named argument of the help's texts, carried inside the help's translatable components until
 * {@link HelpRenderer} fills the placeholder of its name.
 */
sealed interface HelpArgument extends VirtualComponentRenderer<Void> {
	/**
	 * Makes a named argument, as Commandant's {@code ArgumentFactory}: Commandant passes a text, a number or a
	 * component.
	 *
	 * @param name  the placeholder's name
	 * @param value the value
	 * @return the argument
	 */
	@NotNull
	static ComponentLike create(@NotNull String name, @NotNull Object value) {
		HelpArgument argument = value instanceof ComponentLike component
				? new Shown(name, component.asComponent())
				: new Written(name, String.valueOf(value));

		return Component.virtual(Void.class, argument);
	}

	/**
	 * An argument written into the text before it is parsed, such as a command's name or a page number.
	 *
	 * @param name  the placeholder's name
	 * @param value the text
	 */
	record Written(@NotNull String name, @NotNull String value) implements HelpArgument {
		@Override
		public @NotNull ComponentLike apply(Void context) {
			return Component.text(value);
		}
	}

	/**
	 * An argument put into the parsed text as it is, such as an arrow that runs a command.
	 *
	 * @param name  the placeholder's name
	 * @param value the component
	 */
	record Shown(@NotNull String name, @NotNull Component value) implements HelpArgument {
		@Override
		public @NotNull ComponentLike apply(Void context) {
			return value;
		}
	}
}
