package me.whereareiam.socialismus.common.requirement.type;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * {@code language: [de, pt_br]}: holds when the language of the player's game client is one of those listed. A
 * language alone, such as {@code de}, takes every country's variant of it; one with a country, such as
 * {@code pt_br} or {@code pt-BR}, only that one. Case does not matter.
 */
public final class LanguageCondition implements ConditionType.Single<SocialismusPlayer, LanguageCondition.Settings> {
	@Override
	public @NotNull String name() {
		return "language";
	}

	@Override
	public @NotNull Class<SocialismusPlayer> subject() {
		return SocialismusPlayer.class;
	}

	@Override
	public @NotNull Class<Settings> settings() {
		return Settings.class;
	}

	@Override
	public @NotNull Set<Class<? extends Reason>> reasons() {
		return Set.of(Outside.class, Inside.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull Settings settings) {
		Locale locale = player.getLocale();
		String language = normalized(locale.getLanguage());
		String variant = normalized(locale.toString());

		boolean listed = settings.languages().stream()
				.map(LanguageCondition::normalized)
				.anyMatch(each -> each.equals(language) || each.equals(variant));

		return listed
				? Outcome.pass(new Inside(variant))
				: Outcome.fail(new Outside(settings.languages(), variant));
	}

	private static String normalized(String language) {
		return language.replace('-', '_').toLowerCase(Locale.ROOT);
	}

	/**
	 * The condition's settings.
	 *
	 * @param languages the languages, alone or with a country
	 */
	public record Settings(@NotNull List<String> languages) {
	}

	/**
	 * The player's language is none of those listed: {@code language.outside}.
	 *
	 * @param languages the languages listed
	 * @param language  the player's language, such as {@code en_us}
	 */
	public record Outside(@NotNull List<String> languages, @NotNull String language) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("languages", String.join(", ", languages), "language", language);
		}
	}

	/**
	 * The player's language is listed, told when a {@code not} around the condition fails: {@code language.inside}.
	 *
	 * @param language the player's language, such as {@code de_de}
	 */
	public record Inside(@NotNull String language) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("language", language);
		}
	}
}
