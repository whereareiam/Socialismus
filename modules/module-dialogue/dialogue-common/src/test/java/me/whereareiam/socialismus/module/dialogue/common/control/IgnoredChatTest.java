package me.whereareiam.socialismus.module.dialogue.common.control;

import me.whereareiam.socialismus.event.chat.recipient.RecipientsSelectedEvent;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.SenderPrivileges;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import me.whereareiam.socialismus.module.dialogue.common.TestDialogueConditions;
import me.whereareiam.socialismus.module.dialogue.common.store.InMemoryIgnoreStore;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class IgnoredChatTest {
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();
	private final InMemoryIgnoreStore ignores = new InMemoryIgnoreStore();
	private final IgnoredChat ignoredChat = new IgnoredChat(() -> settings, ignores, new SenderPrivileges(TestDialogueConditions.bound(settings)));
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");
	private final SocialismusPlayer carol = TestDataBuilder.mockPlayer("Carol");

	@Test
	void keepsTheChatOfAnIgnoredPlayerFromWhoIgnoresThem() {
		settings.getControl().getIgnore().setIncludesChat(true);
		ignores.ignore(bob.getUniqueId(), new IgnoredPlayer(alice.getUniqueId(), "Alice"));

		assertEquals(Set.of(alice, carol), recipientsOf(alice));
	}

	@Test
	void leavesChatAloneWhenIgnoringCoversOnlyPrivateMessages() {
		ignores.ignore(bob.getUniqueId(), new IgnoredPlayer(alice.getUniqueId(), "Alice"));

		assertEquals(Set.of(alice, bob, carol), recipientsOf(alice));
	}

	@Test
	void leavesTheChatOfAnExemptPlayerAlone() {
		settings.getControl().getIgnore().setIncludesChat(true);
		ignores.ignore(bob.getUniqueId(), new IgnoredPlayer(alice.getUniqueId(), "Alice"));
		when(alice.hasPermission("socialismus.dialogue.ignore.exempt")).thenReturn(true);

		assertEquals(Set.of(alice, bob, carol), recipientsOf(alice));
	}

	private Set<SocialismusPlayer> recipientsOf(SocialismusPlayer sender) {
		Set<SocialismusPlayer> recipients = new HashSet<>(Set.of(alice, bob, carol));
		ignoredChat.onRecipientsSelected(new RecipientsSelectedEvent(ChatMessage.builder().sender(sender).build(), recipients, false));

		return recipients;
	}
}
