package me.whereareiam.socialismus.common.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.service.SerializationService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SerializationServiceAdapter implements SerializationService {
	private final Configura configura = Config.builder()
			.format(Format.JSON)
			.build();

	@Override
	public <T> byte[] serialize(T object) {
		return configura.writeBytes(object);
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		return configura.read(data, clazz);
	}
}
