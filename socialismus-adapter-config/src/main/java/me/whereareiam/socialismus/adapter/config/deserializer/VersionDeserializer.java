package me.whereareiam.socialismus.adapter.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Constants;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.type.Version;

import java.io.IOException;

@Singleton
public class VersionDeserializer extends JsonDeserializer<Version> {
	private final PlatformInteractor interactor;

	@Inject
	public VersionDeserializer(PlatformInteractor interactor) {
		this.interactor = interactor;
	}

	@Override
	public Version deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
		JsonNode node = parser.getCodec().readTree(parser);
		String value = node.asText();

		if (value.equals(Version.UNSUPPORTED.toString())) {
			throw new IOException("Unsupported version: " + value);
		}

		if ("ALL".equalsIgnoreCase(value)) {
			return Constants.SERVER_VERSION;
		}

		try {
			return Version.valueOf(value);
		} catch (IllegalArgumentException e) {
			throw new IOException("Invalid version value: " + value, e);
		}
	}
}
