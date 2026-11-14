package me.whereareiam.socialismus.module.mentionator.common;

import me.whereareiam.conditia.ConditionScope;
import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.common.ConditionsOptions;
import me.whereareiam.conditia.common.DefaultConditions;
import me.whereareiam.conditia.model.Node;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.result.Reason;
import me.whereareiam.conditia.testing.TestSettings;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.model.SerializerOptions;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.common.condition.TargetedCondition;
import me.whereareiam.socialismus.service.Phrasebook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * What Mentionator's tests share: players with permissions, conditions with a {@code permission} type like
 * Socialismus's and Mentionator's own, phrases of which none exist, and a serializer that reads MiniMessage.
 */
public final class TestMentionator {
	private TestMentionator() {
	}

	public static SocialismusPlayer player(String name, String... permissions) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		UUID id = UUID.nameUUIDFromBytes(name.getBytes());
		when(player.getUniqueId()).thenReturn(id);
		when(player.getUsername()).thenReturn(name);
		when(player.hasPermission(anyString())).thenAnswer(call -> Set.of(permissions).contains(call.getArgument(0, String.class)));
		when(player.canSee(any())).thenReturn(true);

		return player;
	}

	public static ChatMessage message(SocialismusPlayer sender, String text) {
		return ChatMessage.builder()
				.id(1)
				.sender(sender)
				.recipients(new ArrayList<>())
				.content(Component.text(text))
				.chat(InternalChat.builder().id("global").build())
				.build();
	}

	public static Conditions conditions() {
		Conditions conditions = new DefaultConditions(ConditionsOptions.builder()
				.reader(new TestSettings().read(Permission.Settings.class, node -> new Permission.Settings(TestSettings.texts(node, "permissions"))))
				.build());
		conditions.register(new Permission());
		ConditionScope mentionator = conditions.scope("mentionator");
		mentionator.register(new TargetedCondition());

		return conditions;
	}

	public static Requirements permission(String permission) {
		return Requirements.of(new Node.Sequence(List.of(Node.Mapping.of("permission", new Node.Scalar(permission)))));
	}

	public static Requirements targeted() {
		return Requirements.of(new Node.Sequence(List.of(Node.Mapping.of(
				"mentionator:targeted",
				new Node.Sequence(List.of(new Node.Scalar("recipient"), new Node.Scalar("message")))
		))));
	}

	public static Phrasebook phrases() {
		return new Phrasebook() {
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

	/**
	 * Reads texts as MiniMessage, with their placeholders filled in first.
	 */
	public static void useMiniMessage() {
		SerializerEngine engine = mock(SerializerEngine.class);
		when(engine.serialize(any(SerializerContent.class))).thenAnswer(call -> {
			SerializerContent content = call.getArgument(0, SerializerContent.class);
			String text = content.getMessage();
			for (Map.Entry<String, String> placeholder : content.getPlaceholders().entrySet())
				text = text.replace("{" + placeholder.getKey() + "}", placeholder.getValue());

			return MiniMessage.miniMessage().deserialize(text);
		});
		when(engine.getPlaceholderFormat()).thenReturn(SerializerOptions.PlaceholderFormat.CURLY_BRACES);
		Serializer.initialize(() -> engine);
		Logger.init(mock(LoggingHelper.class));
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
