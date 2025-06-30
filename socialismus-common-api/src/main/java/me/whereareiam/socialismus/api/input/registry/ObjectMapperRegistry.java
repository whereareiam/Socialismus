package me.whereareiam.socialismus.api.input.registry;

public interface ObjectMapperRegistry {
	<T> void addSerializer(Class<T> type, Object serializer);

	<T> void addDeserializer(Class<T> type, Object deserializer);
}