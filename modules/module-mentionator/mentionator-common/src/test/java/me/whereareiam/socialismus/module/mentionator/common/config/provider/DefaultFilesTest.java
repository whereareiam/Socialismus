package me.whereareiam.socialismus.module.mentionator.common.config.provider;

import me.whereareiam.conditia.ConditionScope;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.adapter.configura.ConditiaFeature;
import me.whereareiam.conditia.adapter.configura.ConfiguraSettings;
import me.whereareiam.conditia.common.ConditionsOptions;
import me.whereareiam.conditia.common.DefaultConditions;
import me.whereareiam.conditia.model.NoSettings;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.feature.polymorphic.PolymorphicFeature;
import me.whereareiam.configura.feature.postprocess.PostProcessFeature;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.common.condition.type.PermissionCondition;
import me.whereareiam.socialismus.common.condition.type.WorldCondition;
import me.whereareiam.socialismus.common.condition.type.recording.CooldownCondition;
import me.whereareiam.socialismus.common.condition.type.recording.Cooldowns;
import me.whereareiam.socialismus.common.condition.type.relation.NearbyCondition;
import me.whereareiam.socialismus.common.condition.type.relation.SameCondition;
import me.whereareiam.socialismus.common.condition.type.relation.SeesCondition;
import me.whereareiam.socialismus.common.config.SerializationServiceAdapter;
import me.whereareiam.socialismus.common.config.SocialismusConfiguraModule;
import me.whereareiam.socialismus.common.store.DefaultStores;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.api.model.config.MentionatorSettings;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.Notification;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.BossbarContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.SoundContent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.content.TitleContent;
import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import me.whereareiam.socialismus.module.mentionator.common.condition.Catalog;
import me.whereareiam.socialismus.module.mentionator.common.condition.ReachedCondition;
import me.whereareiam.socialismus.module.mentionator.common.condition.TargetedCondition;
import me.whereareiam.socialismus.module.mentionator.common.config.defaults.MentionatorSettingsDefaults;
import me.whereareiam.socialismus.module.mentionator.common.config.defaults.NotificationsDefaults;
import me.whereareiam.socialismus.module.mentionator.common.config.defaults.TagsDefaults;
import me.whereareiam.socialismus.module.mentionator.common.config.dynamic.NotificationsConfig;
import me.whereareiam.socialismus.module.mentionator.common.config.dynamic.TagsConfig;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static me.whereareiam.socialismus.module.mentionator.common.TestMentionator.phrases;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * The default files as a server writes them on its first start and reads them back: every tag and content keeps its
 * kind, and every list binds with Socialismus's conditions, Mentionator's own and Dialogue's.
 */
class DefaultFilesTest {
	private final Configura configura = Config.builder()
			.format(Format.YAML)
			.module(new SocialismusConfiguraModule())
			.feature(PostProcessFeature.defaults())
			.feature(PolymorphicFeature.defaults())
			.feature(new ConditiaFeature())
			.build();

	@TempDir
	Path folder;

	@Test
	void theDefaultTagsAreWrittenAndReadBack() throws Exception {
		List<Tag> tags = tags();

		// Files are read in the order of their names: groups, players, roles.
		assertEquals(List.of("everyone", "here", "server", "nearby", "player", "nickname", "staff", "rank"), tags.stream().map(Tag::getId).toList());
		assertFalse(tags.get(5).isEnabled());
		assertEquals(Duration.ofMinutes(1), tags.get(7).getParameters().getRefresh());
		// Kept under build/ for a look at the files as admins get them.
		copy(Path.of("build", "default-files"));
		String players = Files.readString(folder.resolve("tags/players.yml"));
		assertTrue(players.contains("dialogue:ignores"), players);
	}

	@Test
	void theDefaultContentsKeepTheirKind() {
		List<Notification> notifications = notifications();

		Notification alert = notifications.stream().filter(each -> each.getId().equals("alert")).findFirst().orElseThrow();
		assertInstanceOf(SoundContent.class, alert.getContents().get(0));
		TitleContent title = assertInstanceOf(TitleContent.class, alert.getContents().get(1));
		assertEquals(40, title.getParameters().getStay());
		Notification everyone = notifications.stream().filter(each -> each.getId().equals("everyone")).findFirst().orElseThrow();
		assertEquals(Duration.ofSeconds(8), assertInstanceOf(BossbarContent.class, everyone.getContents().get(1)).getParameters().getDuration());
	}

	@Test
	void everyDefaultListBinds() {
		MentionatorSettings settings = new MentionatorSettingsDefaults().supply(new MentionatorSettings());

		try (Catalog catalog = Catalog.bind(conditions(), phrases().scope("mentionator"), tags(), notifications(), settings)) {
			assertEquals(6, catalog.getTags().size());
			assertEquals("everyone", catalog.getTags().getFirst().getId());
		}
	}

	private void copy(Path target) throws java.io.IOException {
		notifications();
		try (var files = Files.walk(folder)) {
			for (Path file : files.filter(Files::isRegularFile).toList()) {
				Path copied = target.resolve(folder.relativize(file));
				Files.createDirectories(copied.getParent());
				Files.copy(file, copied, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	private List<Tag> tags() {
		return ConfigFolder.read(configura, folder.resolve("tags"), TagsConfig.class, () -> TagsDefaults.files(configura, true, true))
				.stream()
				.flatMap(file -> file.getTags().stream())
				.toList();
	}

	private List<Notification> notifications() {
		return ConfigFolder.read(configura, folder.resolve("notifications"), NotificationsConfig.class, () -> NotificationsDefaults.files(true))
				.stream()
				.flatMap(file -> file.getNotifications().stream())
				.toList();
	}

	private Conditions conditions() {
		Conditions conditions = new DefaultConditions(ConditionsOptions.builder()
				.reader(new ConfiguraSettings(configura.mapper()))
				.wordings(Arrays.stream(ConditionTexts.Word.values()).map(ConditionTexts.Word::wording).toList())
				.build());
		conditions.register(new PermissionCondition());
		conditions.register(new SameCondition(() -> mock(PlayerRegistry.class), null));
		conditions.register(new SeesCondition());
		conditions.register(new CooldownCondition(new Cooldowns(new DefaultStores(new SerializationServiceAdapter()), Clock.systemUTC())));
		conditions.register(new WorldCondition());
		conditions.register(new NearbyCondition());
		ConditionScope mentionator = conditions.scope("mentionator");
		mentionator.register(new TargetedCondition());
		mentionator.register(new ReachedCondition(mock(ChatConditionService.class)));
		conditions.scope("dialogue").register(new Ignores());

		return conditions;
	}

	private static final class Ignores implements ConditionType.Pair<SocialismusPlayer, SocialismusPlayer, NoSettings> {
		public @NotNull String name() {
			return "ignores";
		}

		public @NotNull Class<SocialismusPlayer> first() {
			return SocialismusPlayer.class;
		}

		public @NotNull Class<SocialismusPlayer> second() {
			return SocialismusPlayer.class;
		}

		public @NotNull Class<NoSettings> settings() {
			return NoSettings.class;
		}

		public @NotNull Outcome holds(@NotNull SocialismusPlayer first, @NotNull SocialismusPlayer second, @NotNull NoSettings settings) {
			return Outcome.fail();
		}
	}
}
