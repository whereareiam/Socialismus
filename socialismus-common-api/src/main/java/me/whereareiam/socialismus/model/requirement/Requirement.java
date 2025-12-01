package me.whereareiam.socialismus.model.requirement;

import lombok.*;
import lombok.experimental.SuperBuilder;
import me.whereareiam.configura.annotation.Polymorphic;
import me.whereareiam.socialismus.api.model.requirement.type.*;
import me.whereareiam.socialismus.model.requirement.type.*;
import me.whereareiam.socialismus.type.requirement.RequirementConditionType;

/**
 * Represents a requirement condition that can be checked against a specific value.
 * This class is used to define validation rules or access conditions within the plugin.
 *
 * <p>A requirement consists of two main components:</p>
 * <ul>
 *   <li>A condition type that determines how the comparison should be performed</li>
 *   <li>An expected value that serves as the comparison target</li>
 * </ul>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Polymorphic(
		inferBy = {
				@Polymorphic.Infer(field = "servers", target = ServerRequirement.class),
				@Polymorphic.Infer(field = "worlds", target = WorldRequirement.class),
				@Polymorphic.Infer(field = "chatIdentifiers", target = ChatRequirement.class),
				@Polymorphic.Infer(field = "placeholders", target = PlaceholderRequirement.class),
				@Polymorphic.Infer(field = "permissions", target = PermissionRequirement.class)
		}
)
public class Requirement {
	/**
	 * The type of condition to be checked
	 */
	private RequirementConditionType condition;

	/**
	 * The expected value to compare against
	 */
	private String expected;
}