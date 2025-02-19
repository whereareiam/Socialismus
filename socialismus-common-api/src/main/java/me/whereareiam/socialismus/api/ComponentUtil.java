package me.whereareiam.socialismus.api;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Utility class for handling text component conversions in the Socialismus plugin.
 * Provides methods and serializers for converting between different text formats:
 * plain text, MiniMessage, legacy formatting, and Gson.
 *
 * <p>This class uses Adventure API's component system for text formatting
 * and supports various serialization formats including legacy color codes
 * (both &amp; and \u00A7), MiniMessage format, and Gson.</p>
 */
@SuppressWarnings("unused")
public class ComponentUtil {
    /**
     * Serializer for plain text without formatting.
     */
    @Getter
    private static final PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText();

    /**
     * Serializer for MiniMessage format.
     */
    @Getter
    private static final MiniMessage MINI_MESSAGE_SERIALIZER = MiniMessage.miniMessage();

    /**
     * Serializer for legacy format using ampersand (&amp;).
     */
    @Getter
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    /**
     * Serializer for legacy format using section symbol (§).
     */
    @Getter
    private static final LegacyComponentSerializer LEGACY_SECTION_SERIALIZER = LegacyComponentSerializer.legacySection();

    /**
     * Standard Gson serializer for components.
     */
    @Getter
    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.gson();

    /**
     * Gson serializer with color downsampling.
     */
    @Getter
    private static final GsonComponentSerializer GSON_DOWNSAMPLING_SERIALIZER = GsonComponentSerializer.colorDownsamplingGson();

    /**
     * Converts a component to plain text without formatting.
     *
     * @param component the component to convert
     * @return plain text string
     */
    public static String toPlain(Component component) {
        return PLAIN_TEXT_SERIALIZER.serialize(component);
    }

    /**
     * Converts a component to legacy format using ampersand.
     *
     * @param component the component to convert
     * @return formatted string with ampersand color codes
     */
    public static String toString(Component component) {
        return toString(component, false);
    }

    /**
     * Converts a component to legacy format.
     *
     * @param component the component to convert
     * @param section true to use section symbol (§), false for ampersand (&amp;)
     * @return formatted string with color codes
     */
    public static String toString(Component component, boolean section) {
        if (section) return LEGACY_SECTION_SERIALIZER.serialize(component);
        return LEGACY_SERIALIZER.serialize(component);
    }

    /**
     * Converts a MiniMessage formatted string to a component.
     *
     * @param string MiniMessage formatted string
     * @return parsed component
     */
    public static Component toMiniMessage(String string) {
        return MINI_MESSAGE_SERIALIZER.deserialize(string);
    }

    /**
     * Converts a Gson string to a component.
     *
     * @param string Gson formatted string
     * @return parsed component
     */
    public static Component toGson(String string) {
        return toGson(string, false);
    }

    /**
     * Converts a Gson string to a component with optional color downsampling.
     *
     * @param string Gson formatted string
     * @param downsampling whether to use color downsampling
     * @return parsed component
     */
    public static Component toGson(String string, boolean downsampling) {
        if (downsampling) return GSON_DOWNSAMPLING_SERIALIZER.deserialize(string);
        return GSON_SERIALIZER.deserialize(string);
    }
}