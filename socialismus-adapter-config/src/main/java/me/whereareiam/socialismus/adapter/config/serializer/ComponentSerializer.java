package me.whereareiam.socialismus.adapter.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
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
		if (value == null) {
			gen.writeNull();
			return;
		}

		String json = ComponentUtil.toGson(value);

		ObjectCodec codec = gen.getCodec();

		JsonParser parser = codec.getFactory().createParser(json);
		JsonNode tree = codec.readTree(parser);

		gen.writeTree(tree);
	}
}
