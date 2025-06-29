package me.whereareiam.socialismus.adapter.config.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.output.SerializationService;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SerializationServiceAdapter implements SerializationService {
	private final ObjectMapper objectMapper;

	@Override
	public <T> byte[] serialize(T object) throws Exception {
		return objectMapper.writeValueAsBytes(object);
	}

	@Override
	public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
		return objectMapper.readValue(data, clazz);
	}
}