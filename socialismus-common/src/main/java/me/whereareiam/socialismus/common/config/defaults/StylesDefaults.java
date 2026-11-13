package me.whereareiam.socialismus.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.condition.ConditionLists;
import me.whereareiam.socialismus.model.style.Style;
import me.whereareiam.socialismus.model.style.StylesConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * The styles Socialismus ships: {@code basic} with colours and the plain decorations, and {@code full} with every tag
 * a player may use. The default chats accept both.
 */
@Singleton
public class StylesDefaults implements DefaultsProvider<StylesConfig> {
	@Override
	public StylesConfig supply(StylesConfig styles) {
		Style basic = Style.builder()
				.id("basic")
				.tags(new ArrayList<>(List.of("color", "bold", "italic", "underlined", "strikethrough")))
				.requires(ConditionLists.permission("socialismus.style.basic"))
				.build();
		Style full = Style.builder()
				.id("full")
				.tags(new ArrayList<>(List.of("*")))
				.requires(ConditionLists.permission("socialismus.style.full"))
				.build();

		styles.setStyles(new ArrayList<>(List.of(basic, full)));

		return styles;
	}
}
