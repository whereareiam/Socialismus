package me.whereareiam.socialismus.module.bubbler.api.model.requirement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.requirement.Requirement;

import java.util.List;

/**
 * Requirement that checks which activator type triggered a bubble.
 * Used to distinguish between bubbles activated via chat vs command.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ActivatorRequirement extends Requirement {
	/**
	 * List of activator type names to check against (e.g., "CHAT", "COMMAND")
	 */
	private List<String> activators;
}
