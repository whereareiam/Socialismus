package me.whereareiam.socialismus.adapter.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.util.ComponentUtil;
import net.kyori.adventure.text.Component;

import java.io.IOException;

@Singleton
public class ComponentDeserializer extends JsonDeserializer<Component> {
	@Override
	public Component deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
		String json = parser.readValueAsTree().toString();
		return ComponentUtil.toGson(json);
	}
}
