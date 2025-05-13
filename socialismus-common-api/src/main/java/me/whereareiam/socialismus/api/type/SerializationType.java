package me.whereareiam.socialismus.api.type;

import me.whereareiam.socialismus.api.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;

/**
 * Enum defining different types of component serialization methods.
 * Each type represents a different format for serializing Adventure Components.
 *
 * <p>Available serialization types:</p>
 * <ul>
 *   <li>{@link #LEGACY_AMPERSAND}: Legacy format using '&amp;' color codes</li>
 *   <li>{@link #LEGACY_SECTION}: Legacy format using '§' section symbols</li>
 *   <li>{@link #MINIMESSAGE}: MiniMessage format for modern text components</li>
 *   <li>{@link #GSON}: Standard GSON serialization</li>
 *   <li>{@link #GSON_DOWNSAMPLING}: GSON serialization with downsampling support</li>
 * </ul>
 */
public enum SerializationType {
	/**
	 * Uses '&amp;' color codes for legacy text formatting
	 */
	LEGACY_AMPERSAND,

	/**
	 * Uses '§' section symbols for legacy text formatting
	 */
	LEGACY_SECTION,

	/**
	 * Uses MiniMessage format for modern component serialization
	 */
	MINIMESSAGE,

	/**
	 * Uses standard GSON serialization
	 */
	GSON,

	/**
	 * Uses GSON serialization with downsampling capabilities
	 */
	GSON_DOWNSAMPLING;

	/**
	 * Gets the appropriate component serializer for this type.
	 *
	 * @return the component serializer corresponding to this type
	 */
	public ComponentSerializer<Component, ?, String> getSerializer() {
		return switch (this) {
			case LEGACY_AMPERSAND -> ComponentUtil.getLEGACY_SERIALIZER();
			case LEGACY_SECTION -> ComponentUtil.getLEGACY_SECTION_SERIALIZER();
			case MINIMESSAGE -> ComponentUtil.getMINI_MESSAGE_SERIALIZER();
			case GSON -> ComponentUtil.getGSON_SERIALIZER();
			case GSON_DOWNSAMPLING -> ComponentUtil.getGSON_DOWNSAMPLING_SERIALIZER();
		};
	}
}