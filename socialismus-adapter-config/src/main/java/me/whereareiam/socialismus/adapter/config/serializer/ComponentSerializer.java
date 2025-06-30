package me.whereareiam.socialismus.adapter.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.util.ComponentUtil;
import net.kyori.adventure.text.Component;

import java.io.IOException;

@Singleton
public class ComponentSerializer extends JsonSerializer<Component> {
	@Override
	public void serialize(Component value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		String json = ComponentUtil.toGson(value);
		gen.writeRawValue(json);
	}
}
