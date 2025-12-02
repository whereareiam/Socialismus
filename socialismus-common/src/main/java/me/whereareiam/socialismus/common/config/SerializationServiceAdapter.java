package me.whereareiam.socialismus.common.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.configura.reader.ConfigReader;
import me.whereareiam.configura.type.Format;
import me.whereareiam.configura.writer.ConfigWriter;
import me.whereareiam.socialismus.service.SerializationService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SerializationServiceAdapter implements SerializationService {
	private final ConfigReader reader = Config.reader(Format.JSON);
	private final ConfigWriter writer = Config.writer(Format.JSON);

	@Override
	public <T> byte[] serialize(T object) {
		return writer.encode(object);
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) {
		return reader.decode(data, clazz);
	}
}