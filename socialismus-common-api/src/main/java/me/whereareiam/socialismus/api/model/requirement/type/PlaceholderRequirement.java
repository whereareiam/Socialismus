package me.whereareiam.socialismus.api.model.requirement.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.requirement.Requirement;

import java.util.List;

/**
 * Represents a placeholder-based requirement in the Socialismus plugin system.
 * This class extends {@link Requirement} to specify placeholders that must be available
 * for certain features or messages to be processed.
 *
 * <p>This class uses Lombok annotations for boilerplate code generation
 * and implements the Builder pattern for flexible object creation.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class PlaceholderRequirement extends Requirement {
    /**
     * List of placeholder strings that must be available
     */
    private List<String> placeholders;
}