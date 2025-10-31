package me.whereareiam.socialismus.api.output;

public interface SerializationService {
	<T> byte[] serialize(T object);

	<T> T deserialize(byte[] data, Class<T> clazz);
}