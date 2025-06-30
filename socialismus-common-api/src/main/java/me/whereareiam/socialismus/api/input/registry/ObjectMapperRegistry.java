package me.whereareiam.socialismus.api.input.registry;

import me.whereareiam.socialismus.api.input.serializer.config.ConfigDeserializer;
import me.whereareiam.socialismus.api.input.serializer.config.ConfigSerializer;

public interface ObjectMapperRegistry {
	<T> void addSerializer(Class<T> type, ConfigSerializer<? super T> serializer);

	<T> void addDeserializer(Class<T> type, ConfigDeserializer<? extends T> deserializer);
}