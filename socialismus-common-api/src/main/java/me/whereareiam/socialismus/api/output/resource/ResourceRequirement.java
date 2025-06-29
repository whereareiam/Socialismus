package me.whereareiam.socialismus.api.output.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.socialismus.api.type.ResourceType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequirement {
	private ResourceType type;
	/**
	 * false = hard-error when missing, true = module just gets onEnable() with null
	 */
	private boolean optional;
}
