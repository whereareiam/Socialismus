package me.whereareiam.socialismus.common.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import me.whereareiam.socialismus.common.config.deserializer.ComponentDeserializer;
import me.whereareiam.socialismus.common.config.deserializer.SocialismusPlayerDeserializer;
import me.whereareiam.socialismus.common.config.deserializer.VersionDeserializer;
import me.whereareiam.socialismus.common.config.serializer.ComponentSerializer;
import me.whereareiam.socialismus.common.config.serializer.SocialismusPlayerSerializer;
import me.whereareiam.socialismus.common.config.serializer.VersionSerializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.type.Version;
import net.kyori.adventure.text.Component;

public class SocialismusConfiguraModule extends SimpleModule {
	public SocialismusConfiguraModule() {
		addSerializer(Version.class, new VersionSerializer());
		addDeserializer(Version.class, new VersionDeserializer());

		addSerializer(Component.class, new ComponentSerializer());
		addDeserializer(Component.class, new ComponentDeserializer());

		addSerializer(SocialismusPlayer.class, new SocialismusPlayerSerializer());
		addDeserializer(SocialismusPlayer.class, new SocialismusPlayerDeserializer());
	}
}
