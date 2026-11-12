package me.whereareiam.socialismus.model.config.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The texts of the help's page line. Each takes the named placeholders listed on its field.
 */
@Getter
@Setter
@ToString
public class PaginationMessages {
	/**
	 * The page line, with {@code {current}} and {@code {max}}, the page shown and the last page, and
	 * {@code {previous}} and {@code {next}}, the arrows, which are empty where there is no page to go to.
	 */
	private String line;

	/**
	 * The arrow to the previous page, with {@code {page}}. Clicking it opens that page.
	 */
	private String previous;

	/**
	 * The arrow to the next page, with {@code {page}}. Clicking it opens that page.
	 */
	private String next;
}
