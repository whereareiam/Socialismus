package me.whereareiam.socialismus.module.bubbler.api.type;

/**
 * Defines the entity type used to render chat bubbles.
 * Different types offer varying compatibility and visual features.
 */
public enum BubbleType {
	/** Uses armor stand entities with custom names (legacy support, works on all versions) */
	ARMOR_STAND,
	/** Uses text display entities (1.19.4+, best visual quality and features) */
	TEXT_DISPLAY
}
