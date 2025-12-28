package me.whereareiam.socialismus.model.requirement.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.type.chat.TriggerType;

import java.util.List;

/**
 * Represents a requirement related to chat triggers.
 * This class extends the base {@link Requirement} class and adds specific
 * functionality for checking trigger-related conditions.
 *
 * <p>The requirement is satisfied when a player uses one of the specified
 * trigger types to activate a chat channel.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class TriggerRequirement extends Requirement {
    /**
     * List of trigger types that are required.
     * A player must use one of these triggers to satisfy the requirement.
     */
    private List<TriggerType> triggers;
}
