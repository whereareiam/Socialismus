package me.whereareiam.socialismus.common.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import me.whereareiam.socialismus.util.ComponentUtil;
import net.kyori.adventure.text.Component;

import java.io.IOException;

public final class ComponentSerializer extends StdScalarSerializer<Component> {
	public ComponentSerializer() {
		super(Component.class);
	}

	@Override
	public void serialize(Component value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (value == null) {
			gen.writeNull();
			return;
		}

		gen.writeString(ComponentUtil.toGson(value));
	}
}
