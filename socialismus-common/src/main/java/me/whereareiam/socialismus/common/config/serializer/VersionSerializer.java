package me.whereareiam.socialismus.common.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.type.Version;

import java.io.IOException;

public final class VersionSerializer extends StdScalarSerializer<Version> {
	public VersionSerializer() {
		super(Version.class);
	}

	@Override
	public void serialize(Version value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (value == null) {
			gen.writeNull();
			return;
		}

		if (value.equals(Constants.SERVER_VERSION)) {
			gen.writeString("ALL");
			return;
		}

		if (value.equals(Version.UNSUPPORTED))
			throw new IllegalArgumentException("Cannot serialize unsupported version");

		gen.writeString(value.toString());
	}
}
