package me.whereareiam.socialismus.module.mentionator.common.tagging;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentEditsTest {
	private final Component content = Component.text("hi @Steve ", NamedTextColor.WHITE)
			.append(Component.text("and \\@everyone", NamedTextColor.GRAY));

	@Test
	void theTextIsEveryComponentInOrder() {
		assertEquals("hi @Steve and \\@everyone", ContentEdits.plain(content));
	}

	@Test
	void aPartIsChangedInPlaceAndAnotherRemoved() {
		Component changed = ContentEdits.apply(content, List.of(
				new ContentEdits.Edit(3, 9, Component.text("[Steve]")),
				new ContentEdits.Edit(14, 15, null)
		));

		assertEquals("hi [Steve] and @everyone", PlainTextComponentSerializer.plainText().serialize(changed));
	}

	@Test
	void aPartAcrossTwoComponentsCannotBeChanged() {
		assertTrue(ContentEdits.changeable(content, 3, 9));
		assertFalse(ContentEdits.changeable(content, 7, 12));
	}
}
