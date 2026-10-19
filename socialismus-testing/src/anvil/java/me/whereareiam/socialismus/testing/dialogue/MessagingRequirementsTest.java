package me.whereareiam.socialismus.testing.dialogue;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.permission;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.requirements;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.world;

/**
 * The requirements of Dialogue: who may message whom, what a refused sender is told, and which format each
 * participant sees.
 */
@Tag("dialogue")
class MessagingRequirementsTest {
	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = StaffFormat.class)
	void messageTakesTheLastFormatWhoseRequirementsItsParticipantsMeet(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.command("message Bob from the staff");
		bob.sees("[Staff PM] Alice: from the staff");

		bob.command("message Alice from a player");
		alice.sees("[PM] Bob: from a player");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = StaffOnlyRecipients.class)
	void senderIsToldInDialoguesWordsWhyTheRecipientIsRefused(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.command("message Bob not for you");

		alice.sees("Bob may not receive private messages");
		bob.neverSees("not for you");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = NetherSenders.class)
	void senderIsToldWhyTheyMayNotWriteInTheWordingOfDialoguesMessages(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.command("message Bob from the overworld");

		alice.sees("Private messages stay in world_nether");
		bob.neverSees("from the overworld");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = UnwordedRefusal.class)
	void refusalNothingWordsGetsTheNotAllowedNotice(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.command("message Bob not for you");

		alice.sees("You may not write to Bob");
		bob.neverSees("not for you");
	}

	/**
	 * The recipient sees messages of the staff, who hold the permission, in a format of their own.
	 */
	static final class StaffFormat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put(DialogueFiles.FORMATS, Map.of("received", List.of(
					Map.of("format", "[PM] {senderName}: {message}"),
					Map.of(
							"format", "[Staff PM] {senderName}: {message}",
							"requirements", requirements(permission("socialismus.dialogue.staff"), null)
					)
			)));
		}
	}

	/**
	 * Only the staff, who hold the permission, receive private messages.
	 */
	static final class StaffOnlyRecipients implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put(DialogueFiles.SETTINGS, Map.of("messaging", Map.of(
					"requirements", requirements(null, permission("socialismus.dialogue.staff")))));
		}
	}

	/**
	 * Only players in the nether write private messages, and Dialogue's messages word that in their own way.
	 */
	static final class NetherSenders implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put(DialogueFiles.SETTINGS, Map.of("messaging", Map.of(
					"requirements", requirements(world("world_nether"), null))));
			files.put(DialogueFiles.MESSAGES, Map.of("messaging", Map.of("requirements", Map.of(
					"SENDER", Map.of("socialismus:world", Map.of("required", "Private messages stay in {worlds}"))))));
		}
	}

	/**
	 * Only the staff receive private messages, and neither Dialogue nor Socialismus words a missing permission.
	 */
	static final class UnwordedRefusal implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			new StaffOnlyRecipients().accept(files);
			files.put(DialogueFiles.MESSAGES, Map.of("messaging", Map.of(
					"notAllowed", "You may not write to {recipientName}",
					"requirements", Map.of("RECIPIENT", Map.of("socialismus:permission", Map.of("required", "")))
			)));
			files.put(ServerFiles.MESSAGES, Map.of("requirements", Map.of(
					"socialismus:permission", Map.of("required", ""))));
		}
	}
}
