package me.whereareiam.socialismus.model.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A resource a module needs, as its {@code module.json} lists it under {@code requirements}:
 * <pre>{@code
 * "requirements": [
 *   {
 *     "resource": "socialismus:sync",
 *     "optional": true
 *   }
 * ]
 * }</pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequirement {
	/**
	 * The {@linkplain ResourceKey#getFullKey() full key} of the resource, such as {@code socialismus:sync}.
	 */
	private String resource;

	/**
	 * Whether the module loads without the resource. A required resource that no module has registered when
	 * the module loads keeps the module from loading.
	 */
	private boolean optional;
}
