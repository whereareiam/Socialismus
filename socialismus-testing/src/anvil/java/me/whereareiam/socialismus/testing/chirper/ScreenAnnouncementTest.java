package me.whereareiam.socialismus.testing.chirper;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.capability.screen.model.Title;
import me.whereareiam.anvil.capability.sounds.model.HeardSound;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.chirper.ChirperFiles.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Chirper's announcements beside chat: titles, action bars, boss bars and sounds.
 */
@Tag("chirper")
class ScreenAnnouncementTest {
	@Test
	@Socialismus(modules = Module.CHIRPER, configure = EventStart.class)
	void titleActionBarBossBarAndSoundReachThePlayer(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();

		alice.command("socialismus announce event");

		Title title = alice.seesTitle("Event started");
		assertEquals("Gather at spawn", title.getSubtitle());
		// Chirper's times are milliseconds; the client counts ticks of 50 milliseconds.
		assertEquals(4, title.getFadeInTicks());
		assertEquals(40, title.getStayTicks());
		assertEquals(10, title.getFadeOutTicks());

		alice.seesActionBar("Doors open");

		alice.seesBossBar("Event countdown");
		alice.seesBossBarRemoved("Event countdown");

		HeardSound sound = alice.hears("entity.player.levelup");
		assertEquals(0.5F, sound.getPitch(), 0.01F);
	}

	/**
	 * An announcement with a title, an action bar, a boss bar of two seconds and a sound, sent only by command.
	 */
	static final class EventStart implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			announcers(files, disabledAnnouncer("event"));
			announcements(files, announcement("event", everyone(),
					title("<gold>Event started", "<gray>Gather at spawn", 200, 2000, 500),
					actionBar("<green>Doors open"),
					bossBar("<red>Event countdown", "RED", "PROGRESS", 2),
					sound("entity.player.levelup", 1.0F, 0.5F)));
		}
	}
}
