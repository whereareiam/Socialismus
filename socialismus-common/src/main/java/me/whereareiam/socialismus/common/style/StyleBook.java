package me.whereareiam.socialismus.common.style;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.Conditions;
import me.whereareiam.conditia.Place;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.conditia.model.Situation;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.style.Style;
import me.whereareiam.socialismus.model.style.Styles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static me.whereareiam.socialismus.model.requirement.RequirementParts.SENDER;

/**
 * The styles of {@code styles.yml}, bound: each with the place of its {@code requires} and a reader that knows only
 * its tags. They are bound together and closed together when the file is read again.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class StyleBook implements AutoCloseable {
	private final Map<String, BoundStyle> styles;

	/**
	 * Binds the styles of {@code styles.yml}.
	 *
	 * @param conditions binds the styles' requirements
	 * @param file       the styles
	 * @param legacy     whether legacy codes are read as the tags they stand for
	 * @return the bound styles
	 * @throws RuntimeException naming the path of a style whose tags or requirements cannot be read; the styles bound
	 *                          before are closed
	 */
	static @NotNull StyleBook bind(@NotNull Conditions conditions, @NotNull Styles file, boolean legacy) {
		Map<String, BoundStyle> bound = new HashMap<>();
		List<Style> styles = file.getStyles() == null ? List.of() : file.getStyles();
		try {
			for (int index = 0; index < styles.size(); index++) {
				Style style = styles.get(index);
				String path = "styles[" + index + "]";
				if (style.getId() == null || style.getId().isBlank())
					throw new IllegalArgumentException(path + ": a style needs an id");

				BoundStyle previous = bound.put(style.getId(), BoundStyle.bind(conditions, path, style, legacy));
				if (previous != null) previous.place().close();
			}

			return new StyleBook(Map.copyOf(bound));
		} catch (RuntimeException failure) {
			bound.values().forEach(style -> style.place().close());
			throw failure;
		}
	}

	/**
	 * Chooses the last of the styles listed whose requirements the writer meets.
	 */
	@NotNull Optional<String> styleOf(@NotNull SocialismusPlayer writer, @NotNull List<String> ids) {
		Situation situation = Situation.of(SENDER, writer);
		for (String id : ids.reversed()) {
			BoundStyle style = styles.get(id);
			if (style != null && style.place().check(situation).passed()) return Optional.of(id);
		}

		return Optional.empty();
	}

	/**
	 * Reads a text with a style; plain when the book has no style of that id.
	 */
	@NotNull Component read(@NotNull String text, String id) {
		BoundStyle style = id == null ? null : styles.get(id);
		if (style == null) return Component.text(text);

		return style.read(text);
	}

	@Override
	public void close() {
		styles.values().forEach(style -> style.place().close());
	}

	/**
	 * A style, bound.
	 *
	 * @param place   the style's requirements
	 * @param tags    the tags it allows
	 * @param reader  reads only those tags
	 * @param legacy  whether legacy codes are read as the tags they stand for
	 */
	private record BoundStyle(Place place, Set<StyleTag> tags, MiniMessage reader, boolean legacy) {
		static BoundStyle bind(Conditions conditions, String path, Style style, boolean legacy) {
			Set<StyleTag> tags = StyleTags.read(path, listOf(style.getTags()), listOf(style.getExcluded()));
			MiniMessage reader = MiniMessage.builder()
					.tags(TagResolver.resolver(tags.stream().map(StyleTag::getResolver).toList()))
					.build();
			Requirements requires = style.getRequires() == null ? Requirements.none() : style.getRequires();

			return new BoundStyle(conditions.place(path + ".requires", requires, SENDER), tags, reader, legacy);
		}

		Component read(String text) {
			return reader.deserialize(legacy ? LegacyCodes.toTags(text, tags) : text);
		}

		private static List<String> listOf(List<String> names) {
			return names == null ? List.of() : names;
		}
	}
}
