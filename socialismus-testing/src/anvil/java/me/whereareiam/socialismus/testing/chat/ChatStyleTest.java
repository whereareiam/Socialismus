package me.whereareiam.socialismus.testing.chat;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.anvil.protocol.api.model.text.Text;
import me.whereareiam.anvil.protocol.api.model.text.TextSpan;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.socialismus.model.chat.ChatParameters;
import me.whereareiam.socialismus.model.style.Style;
import me.whereareiam.socialismus.model.style.Styles;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.permission;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What players may format in what they write: the styles of {@code styles.yml} that a chat accepts.
 */
@Tag("chat")
class ChatStyleTest {
	@Test
	@Socialismus(configure = BasicAndStaffStyles.class)
	void aSenderFormatsWithTheTagsOfTheirStyle_AndOthersStayAsTyped(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		bob.chat("<bold>loud</bold> <rainbow>calm</rainbow>");

		Text seen = alice.seesFormatted("[G] Bob: loud <rainbow>calm</rainbow>");
		assertTrue(span(seen, "loud").getStyle().isBold(), "Bob's basic style did not make his text bold: " + seen.spans());
	}

	@Test
	@Socialismus(configure = BasicAndStaffStyles.class)
	void noStyleLetsAPlayerWriteAClick_EvenOneOfEveryTag(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("<rainbow>free</rainbow> <click:run_command:'/op Alice'>diamonds</click>");

		Text seen = bob.seesFormatted("[G] Alice: free <click:run_command:'/op Alice'>diamonds</click>");
		assertFalse(
				seen.spans().stream().anyMatch(part -> part.getStyle().getClick() != null),
				"Alice's message carries a click: " + seen.spans()
		);
	}

	@Test
	@Socialismus(configure = ChatWithoutStyles.class)
	void aChatWithoutStylesShowsTagsAsTyped(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("<bold>plain</bold>");

		Text seen = bob.seesFormatted("[G] Alice: <bold>plain</bold>");
		assertFalse(span(seen, "plain").getStyle().isBold(), "Alice's text was formatted: " + seen.spans());
	}

	private static TextSpan span(Text text, String content) {
		return text.span(content).orElseThrow(() -> new AssertionError("No part of " + text.spans() + " holds " + content));
	}

	/**
	 * Global chat accepts {@code basic}, bold for everyone, and {@code staff}, every tag for the operators.
	 */
	static final class BasicAndStaffStyles implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", "[G] {playerName}: {message}").toBuilder()
					.parameters(ChatParameters.builder().styles(List.of("basic", "staff")).build())
					.build());

			Styles styles = new Styles();
			styles.setStyles(List.of(
					Style.builder().id("basic").tags(List.of("bold")).requires(Requirements.none()).build(),
					Style.builder().id("staff").tags(List.of("*")).requires(permission("socialismus.style.staff")).build()
			));
			files.put("styles.yml", styles);
		}
	}

	/**
	 * Global chat accepts no style.
	 */
	static final class ChatWithoutStyles implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", "[G] {playerName}: {message}"));
		}
	}
}
