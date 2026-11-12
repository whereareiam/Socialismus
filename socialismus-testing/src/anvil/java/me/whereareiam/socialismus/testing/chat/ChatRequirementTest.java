package me.whereareiam.socialismus.testing.chat;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.*;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.content;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.not;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.permission;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.world;

/**
 * Who may send in a chat and who receives it: the {@code requires} and {@code reaches} lists of a chat, and what a
 * sender they refuse is told.
 */
@Tag("chat")
class ChatRequirementTest {
	private static final String NO_CHAT = "No chat takes this message";
	private static final String NO_PERMISSION = "{chat} is not yours: {permissions}";
	private static final String OTHER_WORLD = "This stays in {worlds}";
	private static final String NOT_IN_THE_NETHER = "{chat} is closed in {world}";
	private static final String STAFF_ONLY = "Only the staff write in {chat}";

	@Test
	@Socialismus(configure = StaffChat.class)
	void permissionDecidesWhoMaySendInAChatAndWhoReceivesIt(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol").op();

		alice.chat("#for the staff");
		carol.sees("[S] Alice: for the staff");
		bob.neverSees("for the staff");

		bob.chat("#let me in");
		bob.sees("staff is not yours: socialismus.chat.staff");
		carol.neverSees("let me in");
	}

	@Test
	@Socialismus(configure = OverworldChat.class)
	void worldKeepsAChatToThePlayersOfItsWorlds(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol").travelsTo("minecraft:the_nether");

		alice.chat("up here");
		bob.sees("[O] Alice: up here");
		carol.neverSees("up here");

		carol.chat("down here");
		carol.sees("This stays in world");
		bob.neverSees("down here");
	}

	@Test
	@Socialismus(configure = ClosedInTheNetherChat.class)
	void aNegatedConditionTellsWhyItHeld(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").travelsTo("minecraft:the_nether");

		bob.chat("hello");
		bob.sees("surface is closed in world_nether");
		alice.neverSees("hello");
	}

	@Test
	@Socialismus(configure = WordedStaffChat.class)
	void aLineTellsItsOwnTextAndHint_ReferringToAPhrase(ScenarioContext anvil) {
		Player bob = Player.joins(anvil, "Bob");

		bob.chat("#let me in");
		bob.sees("Only the staff write in staff\nAsk an admin for socialismus.chat.staff");
	}

	@Test
	@Socialismus(configure = ReplyChat.class)
	void theMessageBeingSentSelectsTheChat(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("ping");
		bob.sees("[G] Alice: ping");

		alice.chat("pong");
		bob.sees("[R] Alice: pong");
	}

	/**
	 * Without a fallback chat, a message no chat takes gets the no-chat notice, and one its chat refuses why: the
	 * {@code chat.denied} phrases word a missing permission and a world it is closed in, and leave the worlds it is
	 * open in to the {@code denied} phrases of everything.
	 */
	private static void noFallback(ServerFiles files) {
		files.put(CHAT_SETTINGS, Map.of("fallback", Map.of("enabled", false, "chat", "fallback")));
		files.put(CHAT_MESSAGES, Map.of("noChatMatch", NO_CHAT));
		files.put(PHRASES, Map.of(
				"denied", Map.of("world.outside", OTHER_WORLD),
				"chat", Map.of(
						"staff-only", STAFF_ONLY,
						"denied", Map.of(
								"permission.missing", NO_PERMISSION,
								"world.outside", "",
								"world.inside", NOT_IN_THE_NETHER
						)
				)
		));
	}

	/**
	 * A message starting with {@code #} goes to staff chat, which only the holders of its permission send in and
	 * receive.
	 */
	static final class StaffChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(
					chat("global", 0, "", 0, "[G] {playerName}: {message}"),
					chat("staff", 1, "#", 0, "[S] {playerName}: {message}").toBuilder()
							.requires(permission("socialismus.chat.staff"))
							.reaches(permission("socialismus.chat.staff"))
							.build()
			);
			noFallback(files);
		}
	}

	/**
	 * The only chat belongs to the overworld, for its senders and its recipients.
	 */
	static final class OverworldChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("overworld", 0, "", 0, "[O] {playerName}: {message}").toBuilder()
					.requires(world("world"))
					.reaches(world("world"))
					.build());
			noFallback(files);
		}
	}

	/**
	 * The only chat is closed in the nether: {@code not: { world: world_nether }}.
	 */
	static final class ClosedInTheNetherChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("surface", 0, "", 0, "[S] {playerName}: {message}").toBuilder()
					.requires(not(world("world_nether")))
					.build());
			noFallback(files);
		}
	}

	/**
	 * Staff chat tells a refused sender its own text, a phrase of {@code phrases.yml}, and a hint.
	 */
	static final class WordedStaffChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("staff", 0, "#", 0, "[S] {playerName}: {message}").toBuilder()
					.requires(worded(permission("socialismus.chat.staff"), Map.of(
							"denied", "<ref:staff-only>",
							"hint", "Ask an admin for {permissions}"
					)))
					.build());
			noFallback(files);
		}
	}

	/**
	 * A message containing {@code pong} goes to reply chat; every other one falls back to global chat.
	 */
	static final class ReplyChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(
					chat("global", 0, "", 0, "[G] {playerName}: {message}"),
					chat("reply", 1, "", 0, "[R] {playerName}: {message}").toBuilder()
							.requires(content("pong"))
							.build()
			);
			files.put(CHAT_SETTINGS, Map.of("fallback", Map.of("enabled", true, "chat", "global")));
		}
	}
}
