package me.whereareiam.socialismus.module.dialogue.network;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.event.DialogueDeliveredEvent;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.moderation.Spying;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static me.whereareiam.socialismus.module.dialogue.network.TestNodes.player;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SpyCopiesTest {
	private final TestNodes nodes = new TestNodes();

	@Test
	void theSpiesOnOtherNodesSeeAMessageDeliveredOnOne() {
		Spying lobbySpying = mock(Spying.class);
		Spying survivalSpying = mock(Spying.class);
		SpyCopies lobby = new SpyCopies(nodes.node("lobby"), lobbySpying);
		new SpyCopies(nodes.node("survival"), survivalSpying);
		SocialismusPlayer bob = player("Bob");
		Message message = message();
		when(lobbySpying.isSpiedOn(message, bob)).thenReturn(true);

		lobby.onDelivered(new DialogueDeliveredEvent(message, bob));

		ArgumentCaptor<Message> copied = ArgumentCaptor.forClass(Message.class);
		ArgumentCaptor<SocialismusPlayer> recipient = ArgumentCaptor.forClass(SocialismusPlayer.class);
		verify(survivalSpying).showToSpies(copied.capture(), recipient.capture());
		assertEquals("hello", copied.getValue().getContent());
		assertEquals("Alice", copied.getValue().getSender().getUsername());
		assertEquals("Bob", recipient.getValue().getUsername());
		verify(lobbySpying, never()).showToSpies(any(), any());
	}

	@Test
	void aMessageHiddenFromSpiesStaysOnItsNode() {
		Spying lobbySpying = mock(Spying.class);
		Spying survivalSpying = mock(Spying.class);
		SpyCopies lobby = new SpyCopies(nodes.node("lobby"), lobbySpying);
		new SpyCopies(nodes.node("survival"), survivalSpying);

		lobby.onDelivered(new DialogueDeliveredEvent(message(), player("Bob")));

		verify(survivalSpying, never()).showToSpies(any(), any());
	}

	private static Message message() {
		return Message.builder()
				.id("m-1")
				.sender(player("Alice"))
				.recipientName("Bob")
				.content("hello")
				.timestamp(Instant.now())
				.deliveryStatus(DeliveryStatus.DELIVERED_LOCALLY)
				.build();
	}
}
