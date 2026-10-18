package me.whereareiam.socialismus.module.dialogue.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;

/**
 * What Dialogue does when the server syncs with others, {@code sync} in its working directory. Whether the server
 * syncs at all is Socialismus's {@code sync}.
 */
@Getter
@Setter
@ToString
public class DialogueSync {
	/**
	 * Whether a private message to a player who is not on this server is sent to the other servers.
	 */
	private boolean enabled;

	/**
	 * How long to wait for another server to confirm it delivered a private message before the sender is told the
	 * recipient is not online.
	 */
	private Duration confirmation;
}
