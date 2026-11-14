package me.whereareiam.socialismus.model.chat;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatTest {
	@Test
	void aChatThatSaysNothingAboutItMayBeTurnedOff() {
		assertTrue(read("id: global\nenabled: true\n").getParameters().isToggleable());
		assertTrue(Chat.builder().id("global").build().getParameters().isToggleable());
	}

	@Test
	void aChatMayRefuseToBeTurnedOff() {
		assertFalse(read("id: staff\nenabled: true\nparameters:\n  toggleable: false\n").getParameters().isToggleable());
	}

	@Test
	void aChatAcceptsNoStyleUnlessItListsSome() {
		assertTrue(read("id: global\nenabled: true\n").getParameters().getStyles().isEmpty());
		assertEquals(
				List.of("basic", "vip"),
				read("id: global\nparameters:\n  styles: [basic, vip]\n").getParameters().getStyles()
		);
		assertTrue(read("id: global\nparameters:\n  styles: [basic]\n").getParameters().isToggleable());
	}

	private static Chat read(String yaml) {
		return Config.builder().format(Format.YAML).build()
				.read(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)), Chat.class);
	}
}
