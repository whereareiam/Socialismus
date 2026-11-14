package me.whereareiam.socialismus.module.mentionator.common.tagging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * One way a tag is written, split around the value it names: {@code @{username}} is {@code @}, a player's name and
 * nothing; {@code @everyone} is a literal without a value.
 *
 * @param prefix      what comes before the value, or the whole form when it names none
 * @param placeholder the {@code %placeholder%} whose value it names, {@code null} for none or a name
 * @param username    whether it names a player's name
 * @param suffix      what comes after the value
 */
public record Form(@NotNull String prefix, @Nullable String placeholder, boolean username, @NotNull String suffix) {
	private static final String USERNAME = "{username}";
	private static final Pattern PLACEHOLDER = Pattern.compile("%[^%\\s]+%");

	/**
	 * Reads a form as a tag writes it.
	 *
	 * @param written the form, such as {@code @{username}} or {@code @%luckperms_primary_group_name%}
	 * @return the form
	 */
	public static @NotNull Form of(@NotNull String written) {
		int name = written.indexOf(USERNAME);
		if (name >= 0)
			return new Form(written.substring(0, name), null, true, written.substring(name + USERNAME.length()));

		Matcher placeholder = PLACEHOLDER.matcher(written);
		if (placeholder.find())
			return new Form(written.substring(0, placeholder.start()), placeholder.group(), false, written.substring(placeholder.end()));

		return new Form(written, null, false, "");
	}

	/**
	 * Returns whether the form names a value: a player's name or a placeholder's.
	 */
	public boolean namesValue() {
		return username || placeholder != null;
	}

	/**
	 * Returns whether the form is a value alone, such as {@code {username}}, which ordinary words may match.
	 */
	public boolean bare() {
		return namesValue() && prefix.isEmpty() && suffix.isEmpty();
	}
}
