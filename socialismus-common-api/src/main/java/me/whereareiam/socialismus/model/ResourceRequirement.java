package me.whereareiam.socialismus.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.whereareiam.socialismus.type.ResourceType;

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
