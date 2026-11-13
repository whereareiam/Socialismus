package me.whereareiam.socialismus.module.bubbler.common;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.style.StylePlace;
import me.whereareiam.socialismus.service.style.Styles;
import me.whereareiam.socialismus.service.style.TextStyle;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Styles for tests that do not look at formatting: every place accepts any id and everyone writes plain text, as
 * before the styles are bound.
 */
public final class TestStyles implements Styles {
	@Override
	public @NotNull StylePlace place(@NotNull String path, @NotNull List<String> accepted) {
		return new StylePlace() {
			@Override
			public @NotNull TextStyle of(@NotNull SocialismusPlayer writer) {
				return TextStyle.PLAIN;
			}

			@Override
			public void close() {
			}
		};
	}

	@Override
	public @NotNull TextStyle style(@NotNull String id) {
		return TextStyle.PLAIN;
	}
}
