package me.whereareiam.socialismus.testing.chat;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.*;

/**
 * Who may send in a chat and who receives it: the requirements a chat sets its sender and its recipients, and what a
 * sender they refuse is told.
 */
@Tag("chat")
class ChatRequirementTest {
	private static final String NO_CHAT = "No chat takes this message";
	private static final String NO_PERMISSION = "{chat} is not yours: {permissions}";
	private static final String OTHER_WORLD = "This stays in {worlds}";
	private static final String STAFF_ONLY = "Only staff write in staff chat";

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
	@Socialismus(configure = StaffOnlyChat.class)
	void theReasonOfARequirementGroupIsToldBeforeAnyOtherWording(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		bob.chat("#let me in");
		bob.sees(STAFF_ONLY);
		bob.neverSees("is not yours");
		alice.neverSees("let me in");
	}

	@Test
	@Socialismus(configure = ReplyChat.class)
	void previousMessageSelectsTheChat(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("ping");
		bob.sees("[G] Alice: ping");

		alice.chat("pong");
		bob.sees("[R] Alice: pong");
	}

	/**
	 * Without a fallback chat, a message no chat takes gets the no-chat notice, and one its chat refuses why: chat
	 * words a missing permission itself, and leaves a world to {@code messages.yml}.
	 */
	private static void noFallback(ServerFiles files) {
		files.put(CHAT_SETTINGS, Map.of("fallback", Map.of("enabled", false, "chat", "fallback")));
		files.put(CHAT_MESSAGES, Map.of(
				"noChatMatch", NO_CHAT,
				"requirements", Map.of("SENDER", Map.of(
						"socialismus:permission", Map.of("required", NO_PERMISSION),
						"socialismus:world", Map.of("required", "")
				))
		));
		files.put(MESSAGES, Map.of("requirements", Map.of("socialismus:world", Map.of("required", OTHER_WORLD))));
	}

	/**
	 * A message starting with {@code #} goes to staff chat, which only the holders of its permission send in and
	 * receive.
	 */
	static final class StaffChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			RequirementGroup staff = permission("socialismus.chat.staff");
			files.chats(
					chat("global", 0, "", 0, "[G] {playerName}: {message}"),
					chat("staff", 1, "#", 0, "[S] {playerName}: {message}").toBuilder()
							.requirements(requirements(staff, staff))
							.build()
			);
			noFallback(files);
		}
	}

	/**
	 * Staff chat, whose sender requirements say in their own words who writes there.
	 */
	static final class StaffOnlyChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			RequirementGroup staff = permission("socialismus.chat.staff");
			files.chats(
					chat("global", 0, "", 0, "[G] {playerName}: {message}"),
					chat("staff", 1, "#", 0, "[S] {playerName}: {message}").toBuilder()
							.requirements(requirements(staff.toBuilder().reason(STAFF_ONLY).build(), staff))
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
			RequirementGroup overworld = world("world");
			files.chats(chat("overworld", 0, "", 0, "[O] {playerName}: {message}").toBuilder()
					.requirements(requirements(overworld, overworld))
					.build());
			noFallback(files);
		}
	}

	/**
	 * A message after {@code ping} goes to reply chat; every other one falls back to global chat.
	 */
	static final class ReplyChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(
					chat("global", 0, "", 0, "[G] {playerName}: {message}"),
					chat("reply", 1, "", 0, "[R] {playerName}: {message}").toBuilder()
							.requirements(requirements(previousMessage("ping"), null))
							.build()
			);
			files.put(CHAT_SETTINGS, Map.of("fallback", Map.of("enabled", true, "chat", "global")));
		}
	}
}
