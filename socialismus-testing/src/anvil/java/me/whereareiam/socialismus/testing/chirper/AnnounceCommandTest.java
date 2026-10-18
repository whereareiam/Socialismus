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
 * {@code /socialismus announce}, which sends an announcement right away instead of on its announcer's schedule.
 */
@Tag("chirper")
class AnnounceCommandTest {
	@Test
	@Socialismus(modules = Module.CHIRPER, configure = UnscheduledNews.class)
	void announceSendsTheAnnouncementRightAway(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.command("socialismus announce news");

		alice.sees("Announcement news has been broadcasted.");
		bob.sees("breaking news");
	}

	@Test
	@Socialismus(modules = Module.CHIRPER, configure = UnscheduledNews.class)
	void announceNamesAnAnnouncementThatDoesNotExist(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();

		alice.command("socialismus announce rumours");

		alice.sees("Announcement with id rumours not found.");
		alice.neverSees("breaking news");
	}

	/**
	 * An announcement whose only announcer never runs, so only the command sends it.
	 */
	static final class UnscheduledNews implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			announcers(files, disabledAnnouncer("news"));
			announcements(files, announcement("news", everyone(), message("breaking news")));
		}
	}
}
