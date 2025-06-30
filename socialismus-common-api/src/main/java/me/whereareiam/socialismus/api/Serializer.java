package me.whereareiam.socialismus.api;

import me.whereareiam.socialismus.api.input.serializer.ComponentService;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.serializer.SerializerContent;
import net.kyori.adventure.text.Component;

public class Serializer {
	private static ComponentService service;

	public static void init(ComponentService service) {
		Serializer.service = service;
	}

	public static Component serialize(DummyPlayer dummyPlayer, String message) {
		return service.format(dummyPlayer, message);
	}

	public static Component serialize(SerializerContent content) {
		return service.format(content);
	}
}
