package me.whereareiam.socialismus.common.requirement.type.message;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.util.ComponentUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * {@code content: { contains: [discord.gg] }} or {@code content: { matches: "^[A-Z ]+$" }}: holds when the message's
 * plain text contains one of the texts listed, ignoring case, and matches the pattern somewhere; a setting left out
 * holds.
 */
public final class ContentCondition implements ConditionType.Single<ChatMessage, ContentCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "content";
	}

	@Override
	public @NotNull Class<ChatMessage> subject() {
		return ChatMessage.class;
	}

	@Override
	public @NotNull Class<Settings> settings() {
		return Settings.class;
	}

	@Override
	public @NotNull Optional<String> main() {
		return Optional.of("contains");
	}

	@Override
	public @NotNull Outcome holds(@NotNull ChatMessage message, @NotNull Settings settings) {
		String text = message.getContent() == null ? "" : ComponentUtil.toPlain(message.getContent());

		return Outcome.when(contains(text, settings.contains()) && matches(text, settings.matches()));
	}

	private static boolean contains(String text, @Nullable List<String> parts) {
		if (parts == null || parts.isEmpty()) return true;

		String lower = text.toLowerCase(Locale.ROOT);

		return parts.stream().anyMatch(part -> lower.contains(part.toLowerCase(Locale.ROOT)));
	}

	private static boolean matches(String text, @Nullable Pattern pattern) {
		return pattern == null || pattern.matcher(text).find();
	}

	/**
	 * The condition's settings; a bare value fills {@code contains}.
	 *
	 * @param contains texts one of which the message contains, ignoring case; any text when absent
	 * @param matches  a pattern the message matches somewhere; any text when absent
	 */
	public record Settings(@Nullable List<String> contains, @Nullable Pattern matches) {
	}
}
