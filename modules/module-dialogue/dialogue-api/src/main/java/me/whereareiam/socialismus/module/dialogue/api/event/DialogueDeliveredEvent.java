package me.whereareiam.socialismus.module.dialogue.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Fired on the recipient's server once a private message has been shown to its recipient, whether it was written on
 * this server or reached it from another.
 */
@Getter
@ToString
@AllArgsConstructor
public class DialogueDeliveredEvent implements Event {
	/**
	 * The message.
	 */
	private final @NotNull Message message;

	/**
	 * The recipient, who has seen it.
	 */
	private final @NotNull SocialismusPlayer recipient;
}
