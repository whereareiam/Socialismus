package me.whereareiam.socialismus.api.model.chat.trigger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.chat.ChatTrigger;

/**
 * Specialized trigger representing a command alias (without leading slash).
 */
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class CommandChatTrigger extends ChatTrigger {
	private String command;
}


