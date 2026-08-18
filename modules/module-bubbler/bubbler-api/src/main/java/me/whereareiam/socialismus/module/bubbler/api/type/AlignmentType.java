package me.whereareiam.socialismus.module.bubbler.api.type;

import lombok.Getter;

/**
 * Defines the text alignment options for bubble text display entities.
 * Values correspond to Minecraft's text display alignment flags.
 */
@Getter
public enum AlignmentType {
	/** Center-aligned text (default) */
	CENTER(0x00),
	/** Left-aligned text */
	LEFT(0x08),
	/** Right-aligned text */
	RIGHT(0x10);

	/** The protocol flag value for this alignment */
	private final byte value;

	AlignmentType(int value) {
		this.value = (byte) value;
	}
}
