package me.whereareiam.socialismus.common.style;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * The tags a style may allow players to use, by the name {@code styles.yml} gives them. Tags that act on whoever
 * reads a message ({@code click}, {@code insert}), make the server look something up ({@code nbt},
 * {@code selector}, {@code score}) or break out of the message ({@code newline}, {@code reset}) have no constant,
 * so no style can allow them.
 */
@RequiredArgsConstructor
public enum StyleTag {
	COLOR("color", () -> StandardTags.color()),
	SHADOW("shadow", () -> StandardTags.shadowColor()),
	BOLD("bold", () -> StandardTags.decorations(TextDecoration.BOLD)),
	ITALIC("italic", () -> StandardTags.decorations(TextDecoration.ITALIC)),
	UNDERLINED("underlined", () -> StandardTags.decorations(TextDecoration.UNDERLINED)),
	STRIKETHROUGH("strikethrough", () -> StandardTags.decorations(TextDecoration.STRIKETHROUGH)),
	OBFUSCATED("obfuscated", () -> StandardTags.decorations(TextDecoration.OBFUSCATED)),
	GRADIENT("gradient", () -> StandardTags.gradient()),
	RAINBOW("rainbow", () -> StandardTags.rainbow()),
	TRANSITION("transition", () -> StandardTags.transition()),
	PRIDE("pride", () -> StandardTags.pride()),
	FONT("font", () -> StandardTags.font()),
	HOVER("hover", () -> StandardTags.hoverEvent()),
	LANG("lang", () -> TagResolver.resolver(StandardTags.translatable(), StandardTags.translatableFallback())),
	KEY("key", () -> StandardTags.keybind()),
	SPRITE("sprite", () -> StandardTags.sprite()),
	HEAD("head", () -> StandardTags.sequentialHead());

	/**
	 * The name {@code styles.yml} gives the tag.
	 */
	@Getter
	private final @NotNull String key;

	// Lambdas, not method references, so a tag the server's Adventure lacks fails only when it is asked for.
	private final @NotNull Supplier<TagResolver> standard;

	/**
	 * Returns what reads the tag and its aliases, such as {@code <b>} for {@code <bold>}.
	 *
	 * @return the resolver, empty when the server ships a version of Adventure without the tag, as Paper before
	 *         1.21.4 does for {@code shadow}
	 */
	public @NotNull Optional<TagResolver> resolver() {
		try {
			return Optional.of(standard.get());
		} catch (LinkageError missing) {
			return Optional.empty();
		}
	}
}
