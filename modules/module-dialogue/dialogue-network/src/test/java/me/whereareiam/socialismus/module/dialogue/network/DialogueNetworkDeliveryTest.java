package me.whereareiam.socialismus.module.dialogue.network;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.module.dialogue.common.delivery.LocalDelivery;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageRenderer;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.module.network.common.DefaultNetworkService;
import me.whereareiam.socialismus.module.network.common.replication.DefaultReplicationSystem;
import me.whereareiam.socialismus.module.network.testing.InMemoryHub;
import me.whereareiam.socialismus.module.network.testing.JsonSerialization;
import me.whereareiam.socialismus.module.network.testing.MutableClock;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.Scheduler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Private messages between the nodes of an in-memory network, each with its own Dialogue around a mocked local
 * delivery.
 */
class DialogueNetworkDeliveryTest {
	private final InMemoryHub hub = new InMemoryHub();
	private final MutableClock clock = new MutableClock();

	@BeforeAll
	static void initLogger() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Test
	void aMessageReachesTheNodeOfItsRecipientAndTheSenderSeesItSent() {
		Side lobby = side("lobby");
		Side survival = side("survival");
		Side creative = side("creative");
		SocialismusPlayer bob = player("Bob");
		survival.network.players().host(bob);
		when(survival.local.deliver(any(), eq(false))).thenReturn(DeliveryStatus.DELIVERED_LOCALLY);
		when(survival.registry.getPlayerData("bob")).thenReturn(Optional.of(bob));
		Message message = message("bob");

		lobby.delivery.send(message);

		ArgumentCaptor<Message> delivered = ArgumentCaptor.forClass(Message.class);
		verify(survival.local).deliver(delivered.capture(), eq(false));
		assertEquals("Alice", delivered.getValue().getSender().getUsername());
		assertEquals("hello", delivered.getValue().getContent());
		verify(creative.local, never()).deliver(any(), anyBoolean());
		verify(lobby.renderer).showSent(message, null);
		assertEquals(DeliveryStatus.DELIVERED_REMOTELY, message.getDeliveryStatus());
		assertEquals("Bob", message.getRecipientName());
	}

	@Test
	void theSenderLearnsWhyTheRecipientRefusedTheMessage() {
		Side lobby = side("lobby");
		Side survival = side("survival");
		survival.network.players().host(player("Bob"));
		when(survival.local.deliver(any(), eq(false))).thenReturn(DeliveryStatus.IGNORED);
		Message message = message("Bob");

		lobby.delivery.send(message);

		verify(lobby.notices).undelivered(message.getSender(), "Bob", DeliveryStatus.IGNORED);
		verify(lobby.renderer, never()).showSent(any(), any());
	}

	@Test
	void aRecipientNoNodeHostsIsNotOnline() {
		Side lobby = side("lobby");
		side("survival");
		Message message = message("Nobody");

		lobby.delivery.send(message);

		verify(lobby.notices).undelivered(message.getSender(), "Nobody", DeliveryStatus.RECIPIENT_OFFLINE);
		assertEquals(false, lobby.delivery.mayReach("Nobody"));
	}

	private Side side(String name) {
		NodeId id = NodeId.of(name);
		DefaultReplicationSystem replication = new DefaultReplicationSystem(id, clock, new JsonSerialization(), Runnable::run);
		DefaultNetworkService network = new DefaultNetworkService(replication,
				NetworkNode.builder().id(id).role(NodeRole.SERVER).version("dev").build(), Duration.ofSeconds(15));
		network.attach(hub.transport(name), null);

		LocalDelivery local = mock(LocalDelivery.class);
		MessageRenderer renderer = mock(MessageRenderer.class);
		DialogueNotices notices = mock(DialogueNotices.class);
		PlayerRegistry registry = mock(PlayerRegistry.class);
		when(registry.getPlayerData(any(String.class))).thenReturn(Optional.empty());
		DialogueNetworkDelivery delivery = new DialogueNetworkDelivery(network, local, renderer, notices, registry,
				mock(Scheduler.class), () -> Duration.ofSeconds(3));

		return new Side(network, delivery, local, renderer, notices, registry);
	}

	private static Message message(String recipient) {
		return Message.builder()
				.id(UUID.randomUUID().toString())
				.sender(player("Alice"))
				.recipientName(recipient)
				.content("hello")
				.timestamp(Instant.now())
				.deliveryStatus(DeliveryStatus.PENDING_DELIVERY)
				.conversationId("conversation")
				.build();
	}

	private static SocialismusPlayer player(String name) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		when(player.getUniqueId()).thenReturn(UUID.nameUUIDFromBytes(name.getBytes()));
		when(player.getUsername()).thenReturn(name);

		return player;
	}

	private record Side(
			DefaultNetworkService network,
			DialogueNetworkDelivery delivery,
			LocalDelivery local,
			MessageRenderer renderer,
			DialogueNotices notices,
			PlayerRegistry registry
	) {
	}
}
