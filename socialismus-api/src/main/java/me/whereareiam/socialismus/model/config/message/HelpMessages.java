package me.whereareiam.socialismus.model.config.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The texts of the help command. Each takes the named placeholders listed on its field.
 */
@Getter
@Setter
@ToString
public class HelpMessages {
	/**
	 * The heading above the commands, with {@code {host}}, the plugin's name.
	 */
	private String title;

	/**
	 * One command's line, with {@code {command}}, the root command, {@code {arguments}}, its arguments each after
	 * a space, and {@code {description}}.
	 */
	private String command;

	/**
	 * A required argument inside {@code {arguments}}, with {@code {name}}.
	 */
	private String argument;

	/**
	 * An optional argument inside {@code {arguments}}, with {@code {name}}.
	 */
	private String optionalArgument;

	/**
	 * The line shown instead of commands when a page has none.
	 */
	private String empty;
}
