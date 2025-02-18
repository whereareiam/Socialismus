package me.whereareiam.socialismus.api.model.requirement.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.requirement.Requirement;

import java.util.List;

/**
 * Represents a requirement related to chat channels.
 * This class extends the base {@link Requirement} class and adds specific
 * functionality for checking chat-related conditions.
 *
 * <p>The requirement is satisfied when a player interacts with the specified
 * chat channels identified by their unique identifiers.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ChatRequirement extends Requirement {
    /**
     * List of chat channel identifiers that are required.
     * A player must interact with these chat channels to satisfy the requirement.
     */
    private List<String> chatIdentifiers;
}