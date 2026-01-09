package me.whereareiam.socialismus.model.requirement.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.requirement.Requirement;

import java.util.List;

/**
 * Represents a requirement related to chat messages.
 * This class extends the base {@link Requirement} class and adds specific
 * functionality for checking message-related conditions.
 *
 * <p>The requirement is satisfied when a player's message content matches
 * the specified condition, such as checking if the message is empty or not empty.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class MessageRequirement extends Requirement {
    /**
     * List of message patterns to check against.
     * Used for EQUALS and CONTAINS conditions.
     * For HAS condition, this field can be omitted.
     */
    private List<String> messages;
}
