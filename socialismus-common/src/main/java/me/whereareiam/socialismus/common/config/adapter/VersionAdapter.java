package me.whereareiam.socialismus.common.config.adapter;

import me.whereareiam.configura.TypeAdapter;
import me.whereareiam.socialismus.api.Constants;
import me.whereareiam.socialismus.api.type.Version;

public class VersionAdapter implements TypeAdapter<Version> {
	@Override
	public Version deserialize(String value) {
		String s = value == null ? "" : value.trim();

		if (s.equals(Version.UNSUPPORTED.toString())) {
			throw new IllegalArgumentException("Unsupported version: " + s);
		}

		if ("ALL".equalsIgnoreCase(s)) {
			return Constants.SERVER_VERSION;
		}

		try {
			return Version.valueOf(s);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid version value: " + s, e);
		}
	}

	@Override
	public String serialize(Version value) {
		if (value == null) return null;

		if (value.equals(Constants.SERVER_VERSION)) {
			return "ALL";
		}

		if (value.equals(Version.UNSUPPORTED)) {
			throw new IllegalArgumentException("Cannot serialize unsupported version");
		}

		return value.toString();
	}
}


