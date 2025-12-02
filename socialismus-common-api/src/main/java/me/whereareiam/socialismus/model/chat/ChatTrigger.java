package me.whereareiam.socialismus.model.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.configura.annotation.Polymorphic;
import me.whereareiam.socialismus.type.chat.TriggerType;
import me.whereareiam.socialismus.model.chat.trigger.CommandChatTrigger;
import me.whereareiam.socialismus.model.chat.trigger.RegexChatTrigger;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;

/**
 * Describes a trigger that can route a message into a chat.
 * Common fields are defined here; concrete data lives on subclasses
 * (e.g. {@code SymbolChatTrigger#symbol}, {@code RegexChatTrigger#regex}, {@code CommandChatTrigger#command}).
 * Each trigger can optionally carry a radius override applied for recipient selection.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Polymorphic(
    discriminator = "type",
    mappings = {
        @Polymorphic.Type(value = "SYMBOL", target = SymbolChatTrigger.class),
        @Polymorphic.Type(value = "REGEX", target = RegexChatTrigger.class),
        @Polymorphic.Type(value = "COMMAND", target = CommandChatTrigger.class)
    },
    defaultValue = "SYMBOL"
)
public class ChatTrigger {
    /**
     * Trigger kind.
     */
    private TriggerType type;

    /**
     * If true (default), the matched portion is removed from the message content.
     * Only applicable for SYMBOL and REGEX triggers.
     */
    private boolean strip;

    /**
     * Optional radius in blocks applied for messages using this trigger.
     * If null or <= 0, message is considered global.
     */
    private Integer radius;
}


