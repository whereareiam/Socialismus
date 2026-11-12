package me.whereareiam.socialismus.common.requirement.type;

import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.placeholder.PlaceholderIntegration;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.util.PlaceholderUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * {@code placeholder: { text: "%vault_eco_balance%", above: 100 }}: holds when the player's value of the text, with
 * its placeholders resolved, passes every comparison given: {@code is} a text, {@code matches} a pattern, is
 * {@code above} or {@code below} a number. A value that is no number fails a number's comparison, and without a
 * placeholder plugin the condition fails without a reason.
 */
@RequiredArgsConstructor
public final class PlaceholderCondition implements ConditionType.Single<SocialismusPlayer, PlaceholderCondition.Settings> {
	private final Provider<Set<Integration>> integrations;

	@Override
	public @NotNull String name() {
		return "placeholder";
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
		return Set.of(Mismatch.class, Match.class);
	}

	@Override
	public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull Settings settings) {
		Optional<PlaceholderIntegration> integration = integration();
		if (integration.isEmpty()) return Outcome.fail();

		String value = PlaceholderUtil.resolve(integration.get(), player.getUniqueId(), settings.text());

		return compares(value, settings)
				? Outcome.pass(new Match(settings.text(), value))
				: Outcome.fail(new Mismatch(settings.text(), value));
	}

	private Optional<PlaceholderIntegration> integration() {
		return integrations.get().stream()
				.filter(PlaceholderIntegration.class::isInstance)
				.filter(Integration::isAvailable)
				.map(PlaceholderIntegration.class::cast)
				.findFirst();
	}

	private static boolean compares(String value, Settings settings) {
		if (settings.is() != null && !settings.is().equals(value)) return false;
		if (settings.matches() != null && !settings.matches().matcher(value).matches()) return false;
		if (settings.above() == null && settings.below() == null) return true;

		Optional<Double> number = number(value);
		if (number.isEmpty()) return false;

		return (settings.above() == null || number.get() > settings.above())
				&& (settings.below() == null || number.get() < settings.below());
	}

	private static Optional<Double> number(String value) {
		try {
			return Optional.of(Double.parseDouble(value.trim()));
		} catch (NumberFormatException exception) {
			return Optional.empty();
		}
	}

	/**
	 * The condition's settings: the text and the comparisons its value must pass, any of which may be left out.
	 *
	 * @param text    the text whose placeholders are resolved, such as {@code %player_level%}
	 * @param is      a text the value is
	 * @param matches a pattern the whole value matches
	 * @param above   a number the value is greater than
	 * @param below   a number the value is less than
	 */
	public record Settings(
			@NotNull String text,
			@Nullable String is,
			@Nullable Pattern matches,
			@Nullable Double above,
			@Nullable Double below
	) {
	}

	/**
	 * The value does not pass a comparison: {@code placeholder.mismatch}.
	 *
	 * @param text  the text resolved
	 * @param value the player's value
	 */
	public record Mismatch(@NotNull String text, @NotNull String value) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("text", text, "value", value);
		}
	}

	/**
	 * The value passes every comparison, told when a {@code not} around the condition fails:
	 * {@code placeholder.match}.
	 *
	 * @param text  the text resolved
	 * @param value the player's value
	 */
	public record Match(@NotNull String text, @NotNull String value) implements Reason {
		@Override
		public @NotNull Map<String, String> values() {
			return Map.of("text", text, "value", value);
		}
	}
}
