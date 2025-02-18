package me.whereareiam.socialismus.api.model.requirement.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.requirement.Requirement;

import java.util.List;

/**
 * Represents server-specific requirements for feature access.
 * Extends the base {@link Requirement} class to specify which servers
 * are allowed to use certain features.
 *
 * <p>This requirement checks if a player's current server
 * matches any of the specified server names in the list.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ServerRequirement extends Requirement {
    /** List of server names where the feature is allowed */
    private List<String> servers;
}