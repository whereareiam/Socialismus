package me.whereareiam.socialismus.api.model.requirement.type;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.api.model.requirement.Requirement;

import java.util.List;

/**
 * Requirement class that checks if a player is in specific Minecraft worlds.
 * Extends the base {@link Requirement} class to add world-specific validation.
 *
 * <p>This requirement allows configuration of allowed worlds where certain
 * features or actions can be performed. If a player is not in one of the
 * specified worlds, the requirement will not be met.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class WorldRequirement extends Requirement {
    /** List of world names that satisfy this requirement */
    private List<String> worlds;
}