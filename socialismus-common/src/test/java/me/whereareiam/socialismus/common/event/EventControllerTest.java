package me.whereareiam.socialismus.common.event;

import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.chat.recipient.RecipientsResolvedEvent;
import me.whereareiam.socialismus.event.chat.recipient.RecipientsSelectedEvent;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class EventControllerTest {
	private final EventController events = new EventController();
	private final SocialismusPlayer alice = mock(SocialismusPlayer.class);
	private final SocialismusPlayer bob = mock(SocialismusPlayer.class);

	@BeforeEach
	void setUp() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Test
	void appliesTheRecipientsAListenerTookAwayBeforeTheCallReturns() {
		events.register(new SlowListener(bob));
		Collection<SocialismusPlayer> recipients = new HashSet<>(Set.of(alice, bob));

		events.call(new RecipientsSelectedEvent(ChatMessage.builder().sender(alice).build(), recipients, false));

		assertEquals(Set.of(alice), recipients);
	}

	@Test
	void cancelsTheResolvingOfRecipientsBeforeTheCallReturns() {
		events.register(new SlowListener(bob));
		RecipientsResolvedEvent event = new RecipientsResolvedEvent(ChatMessage.builder().sender(alice).build(), false);

		events.call(event);

		assertTrue(event.isCancelled());
	}

	/**
	 * Changes the events it hears only after a while, as a listener that looks something up does.
	 */
	public static class SlowListener implements EventListener {
		private final SocialismusPlayer removed;

		SlowListener(SocialismusPlayer removed) {
			this.removed = removed;
		}

		@SocialisticEvent
		public void onRecipientsSelected(RecipientsSelectedEvent event) throws InterruptedException {
			Thread.sleep(200);
			event.getNewRecipients().remove(removed);
		}

		@SocialisticEvent
		public void onRecipientsResolved(RecipientsResolvedEvent event) throws InterruptedException {
			Thread.sleep(200);
			event.setCancelled(true);
		}
	}
}
