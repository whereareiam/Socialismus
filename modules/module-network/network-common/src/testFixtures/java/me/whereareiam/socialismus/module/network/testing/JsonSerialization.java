package me.whereareiam.socialismus.module.network.testing;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.service.SerializationService;

/**
 * Serialization as Socialismus does it, as JSON, without its player and component support.
 */
public final class JsonSerialization implements SerializationService {
	private final Configura configura = Config.builder().format(Format.JSON).build();

	@Override
	public <T> byte[] serialize(T object) {
		return configura.writeBytes(object);
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		return configura.read(data, clazz);
	}
}
