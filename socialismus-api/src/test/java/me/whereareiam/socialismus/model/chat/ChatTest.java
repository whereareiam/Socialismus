package me.whereareiam.socialismus.model.chat;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatTest {
	@Test
	void aChatThatSaysNothingAboutItMayBeTurnedOff() {
		assertTrue(read("id: global\nenabled: true\n").isToggleable());
		assertTrue(Chat.builder().id("global").build().isToggleable());
	}

	@Test
	void aChatMayRefuseToBeTurnedOff() {
		assertFalse(read("id: staff\nenabled: true\ntoggleable: false\n").isToggleable());
	}

	private static Chat read(String yaml) {
		return Config.builder().format(Format.YAML).build()
				.read(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)), Chat.class);
	}
}
