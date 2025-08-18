package me.whereareiam.socialismus.api.model.chat.trigger;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.chat.ChatTrigger;

/**
 * Specialized trigger representing a regular expression match.
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class RegexChatTrigger extends ChatTrigger {
	private String pattern;
}


