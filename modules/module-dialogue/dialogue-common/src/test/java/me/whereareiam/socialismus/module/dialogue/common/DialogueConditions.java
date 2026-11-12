package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.common.ConditionsOptions;
import me.whereareiam.conditia.common.DefaultConditions;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.conditia.testing.TestSettings;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.api.model.config.ModerationFormats;
import me.whereareiam.socialismus.module.dialogue.common.requirement.DialogueRequirements;
import me.whereareiam.socialismus.service.phrase.PhraseService;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Dialogue's requirements bound for tests with a {@code permission} type like Socialismus's, so a test grants a mock
 * player a permission instead of stubbing a requirement.
 */
public final class DialogueConditions {
	private DialogueConditions() {
	}

	public static DialogueRequirements bound(DialogueSettings settings) {
		return bound(() -> settings, MessagingFormats::new, ModerationFormats::new);
	}

	public static DialogueRequirements bound(
			Supplier<DialogueSettings> settings,
			Supplier<MessagingFormats> formats,
			Supplier<ModerationFormats> moderation
	) {
		DialogueRequirements requirements = new DialogueRequirements(conditions(), phrases(), settings::get, formats::get, moderation::get);
		requirements.bind();

		return requirements;
	}

	/**
	 * Phrases of which none exist, so a test whose lists refer to none binds them.
	 */
	static PhraseService phrases() {
		return new PhraseService() {
			public @NotNull Optional<String> find(@NotNull String scope, @NotNull String key) {
				return Optional.empty();
			}

			public @NotNull String render(@NotNull String scope, @NotNull String text) {
				return text;
			}

			public @NotNull List<String> unresolved(@NotNull String scope, @NotNull String text) {
				return List.of();
			}

			public void add(@NotNull String owner, @NotNull Supplier<Phrases> phrases) {
			}

			public void remove(@NotNull String owner) {
			}
		};
	}

	static Conditions conditions() {
		Conditions conditions = new DefaultConditions(ConditionsOptions.builder()
				.reader(new TestSettings().read(Permission.Settings.class, node -> new Permission.Settings(TestSettings.texts(node, "permissions"))))
				.build());
		conditions.register(new Permission());

		return conditions;
	}

	static final class Permission implements ConditionType.Single<SocialismusPlayer, Permission.Settings> {
		record Settings(List<String> permissions) {
		}

		record Missing() implements Reason {
		}

		public @NotNull String name() {
			return "permission";
		}

		public @NotNull Class<SocialismusPlayer> subject() {
			return SocialismusPlayer.class;
		}

		public @NotNull Class<Settings> settings() {
			return Settings.class;
		}

		public @NotNull Set<Class<? extends Reason>> reasons() {
			return Set.of(Missing.class);
		}

		public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull Settings settings) {
			return settings.permissions().stream().allMatch(player::hasPermission) ? Outcome.pass() : Outcome.fail(new Missing());
		}
	}
}
