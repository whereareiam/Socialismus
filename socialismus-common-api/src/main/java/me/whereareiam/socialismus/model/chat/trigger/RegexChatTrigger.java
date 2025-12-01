package me.whereareiam.socialismus.model.chat.trigger;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.chat.ChatTrigger;

/**
 * Specialized trigger representing a regular expression match.
 */
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class RegexChatTrigger extends ChatTrigger {
	private String pattern;
}


