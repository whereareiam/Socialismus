package me.whereareiam.socialismus.event.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.CancellableEvent;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.event.base.SynchronousEvent;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player on this server writes in chat, before Socialismus chooses a chat for the message. Cancelling
 * it takes the message out of chat altogether: no chat is chosen, nobody receives it as chat, and the server's own
 * chat event is cancelled. A listener that cancels it handles the message itself.
 *
 * <p>This is how a module turns chat into something else for some players, such as a private conversation:
 * <pre>{@code
 * @SocialisticEvent
 * public void onChat(ChatReceivedEvent event) {
 *     String partner = conversations.partnerOf(event.getChatMessage().getSender());
 *     if (partner == null) return;
 *
 *     event.setCancelled(true);
 *     dialogue.send(event.getChatMessage().getSender(), partner, plainText(event.getChatMessage().getContent()));
 * }
 * }</pre>
 *
 * <p>Messages {@linkplain me.whereareiam.socialismus.service.chat.ChatCoordinationService#relay relayed} from
 * elsewhere, such as another server of a network, do not fire it.
 */
@Getter
@Setter
@ToString
public class ChatReceivedEvent implements Event, CancellableEvent, SynchronousEvent {
	/**
	 * The message as the player wrote it, before a chat is chosen.
	 */
	private final @NotNull ChatMessage chatMessage;

	/**
	 * Whether the message is taken out of chat.
	 */
	private boolean cancelled;

	public ChatReceivedEvent(@NotNull ChatMessage chatMessage) {
		this.chatMessage = chatMessage;
	}
}
