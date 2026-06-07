package me.whereareiam.socialismus.common.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.type.Version;

import java.io.IOException;

public final class VersionDeserializer extends StdScalarDeserializer<Version> {
	public VersionDeserializer() {
		super(Version.class);
	}

	@Override
	public Version deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		String value = parser.getValueAsString("");
		String normalized = value == null ? "" : value.trim();

		if (normalized.equals(Version.UNSUPPORTED.toString()))
			throw new IllegalArgumentException("Unsupported version: " + normalized);

		if ("ALL".equalsIgnoreCase(normalized))
			return Constants.SERVER_VERSION;

		try {
			return Version.valueOf(normalized);
		} catch (IllegalArgumentException exception) {
			throw new IllegalArgumentException("Invalid version value: " + normalized, exception);
		}
	}
}
