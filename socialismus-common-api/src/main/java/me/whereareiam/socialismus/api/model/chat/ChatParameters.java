package me.whereareiam.socialismus.api.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.whereareiam.socialismus.api.type.chat.ChatType;

/**
 * Deprecated in favor of ChatTrigger list on Chat. Left for binary compatibility with modules.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class ChatParameters {
	private ChatType type;
	private String symbol;
	private int radius;
}