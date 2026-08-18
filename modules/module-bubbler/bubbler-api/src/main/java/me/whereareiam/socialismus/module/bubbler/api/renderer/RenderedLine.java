package me.whereareiam.socialismus.module.bubbler.api.renderer;

import java.util.List;

/**
 * Represents a rendered bubble line with its associated entity IDs.
 * A single line may use multiple entities (e.g., a carrier and a display).
 */
public interface RenderedLine {
	/**
	 * Gets all entity IDs associated with this rendered line.
	 * Used for cleanup when destroying the line.
	 *
	 * @return list of all entity IDs
	 */
	List<Integer> getAllEntityIds();

	/**
	 * Gets the primary entity ID for this line.
	 * This is typically the visible display entity.
	 *
	 * @return the primary entity ID
	 */
	int getPrimaryEntityId();
}
