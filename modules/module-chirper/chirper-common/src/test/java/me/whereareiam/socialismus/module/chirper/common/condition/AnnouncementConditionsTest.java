package me.whereareiam.socialismus.module.chirper.common.condition;

import me.whereareiam.conditia.ConditionType;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.common.ConditionsOptions;
import me.whereareiam.conditia.common.DefaultConditions;
import me.whereareiam.conditia.result.Outcome;
import me.whereareiam.conditia.testing.TestSettings;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.variant.MessageAnnouncement;
import me.whereareiam.socialismus.module.chirper.api.type.AnnouncementType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static me.whereareiam.conditia.testing.TestRequirements.yaml;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnnouncementConditionsTest {
	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);
	private final SocialismusPlayer bob = mock(SocialismusPlayer.class);

	@Test
	void anAnnouncementAndEachOfItsContentsReachThePlayersWhoMeetThem() {
		when(alice.hasPermission("vip")).thenReturn(true);
		when(bob.hasPermission("vip")).thenReturn(true);
		when(alice.hasPermission("staff")).thenReturn(true);
		MessageAnnouncement content = MessageAnnouncement.builder().requires(yaml("- permission: staff")).build();
		Announcement announcement = Announcement.builder()
				.id("vip")
				.requires(yaml("- permission: vip"))
				.contents(Map.of(AnnouncementType.MESSAGE, content))
				.build();
		AnnouncementConditions announcementConditions = new AnnouncementConditions(conditions(), () -> List.of(announcement));
		announcementConditions.bind();

		AnnouncementConditions.Places places = announcementConditions.of(announcement).orElseThrow();
		assertTrue(places.reaches(bob));
		assertTrue(places.reaches(AnnouncementType.MESSAGE, alice));
		assertFalse(places.reaches(AnnouncementType.MESSAGE, bob));
		assertFalse(places.reaches(mock(SocialismusPlayer.class)));
	}

	private static Conditions conditions() {
		Conditions conditions = new DefaultConditions(ConditionsOptions.builder()
				.reader(new TestSettings().read(Permission.Settings.class, node -> new Permission.Settings(TestSettings.texts(node, "permissions"))))
				.build());
		conditions.register(new Permission());

		return conditions;
	}

	private static final class Permission implements ConditionType.Single<SocialismusPlayer, Permission.Settings> {
		record Settings(List<String> permissions) {
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

		public @NotNull Outcome holds(@NotNull SocialismusPlayer player, @NotNull Settings settings) {
			return Outcome.when(settings.permissions().stream().allMatch(player::hasPermission));
		}
	}
}
