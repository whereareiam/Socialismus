package me.whereareiam.socialismus.model.requirement.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.requirement.Requirement;

import java.util.List;

/**
 * Represents a permission-based requirement in the Socialismus plugin system.
 * This class extends {@link Requirement} to specify permissions that must be met
 * for certain actions or features to be accessible.
 *
 * <p>This class uses Lombok annotations for boilerplate code generation
 * and implements the Builder pattern for flexible object creation.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class PermissionRequirement extends Requirement {
    /**
     * List of permission strings that must be satisfied
     */
    private List<String> permissions;
}