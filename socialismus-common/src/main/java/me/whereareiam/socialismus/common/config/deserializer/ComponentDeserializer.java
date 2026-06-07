package me.whereareiam.socialismus.common.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import me.whereareiam.socialismus.util.ComponentUtil;
import net.kyori.adventure.text.Component;

import java.io.IOException;

public final class ComponentDeserializer extends StdScalarDeserializer<Component> {
	public ComponentDeserializer() {
		super(Component.class);
	}

	@Override
	public Component deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		String value = parser.getValueAsString();
		if (value == null || value.isEmpty()) return null;

		return ComponentUtil.toGson(value);
	}
}
