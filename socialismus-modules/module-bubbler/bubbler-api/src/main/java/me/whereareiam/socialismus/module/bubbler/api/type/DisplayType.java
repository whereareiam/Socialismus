package me.whereareiam.socialismus.module.bubbler.api.type;

import lombok.Getter;

/**
 * Defines the billboard mode for display entities, controlling how they rotate relative to the viewer.
 * Values correspond to Minecraft's display entity billboard modes.
 */
@Getter
public enum DisplayType {
	/** Display has a fixed rotation, does not track the viewer */
	FIXED(0x00),
	/** Display rotates only on the vertical axis to face the viewer */
	VERTICAL(0x01),
	/** Display rotates only on the horizontal axis to face the viewer */
	HORIZONTAL(0x02),
	/** Display always faces the viewer (full billboard mode) */
	CENTER(0x03);

	/** The protocol value for this billboard mode */
	private final byte value;

	DisplayType(int value) {
		this.value = (byte) value;
	}
}
