package me.whereareiam.socialismus.api.model.chat.trigger;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.chat.ChatTrigger;

/**
 * Specialized trigger representing a literal symbol prefix.
 */
@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class SymbolChatTrigger extends ChatTrigger {
    private String symbol;
}


