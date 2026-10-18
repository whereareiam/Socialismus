package me.whereareiam.socialismus.testing.chirper;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.chirper.ChirperFiles.*;

/**
 * The announcers of Chirper: each broadcasts its announcements one after another on its schedule, to the players
 * who meet each announcement's requirements.
 */
@Tag("chirper")
class ScheduledAnnouncementTest {
	@Test
	@Socialismus(modules = Module.CHIRPER, configure = TwoNews.class)
	void announcerBroadcastsItsAnnouncementsInTurnOnItsSchedule(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");

		alice.sees("first news");
		alice.sees("second news");
		alice.sees("first news");
	}

	@Test
	@Socialismus(modules = Module.CHIRPER, configure = StaffNews.class)
	void requirementsKeepAnAnnouncementToThePlayersWhoMeetThem(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.sees("staff news");
		bob.neverSees("staff news");
		bob.sees("public news");
	}

	/**
	 * An announcer broadcasts two chat announcements in turn, one every two seconds.
	 */
	static final class TwoNews implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			announcers(files, announcer(2, "a-first", "b-second"));
			announcements(files,
					announcement("a-first", everyone(), message("<gold>first news")),
					announcement("b-second", everyone(), message("<gold>second news")));
		}
	}

	/**
	 * An announcer broadcasts, every second, a staff announcement only the holders of its permission receive and a
	 * public one in turn.
	 */
	static final class StaffNews implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			announcers(files, announcer(1, "a-staff", "b-public"));
			announcements(files,
					announcement("a-staff", ServerFiles.permission("journey.staff"), message("staff news")),
					announcement("b-public", everyone(), message("public news")));
		}
	}
}
