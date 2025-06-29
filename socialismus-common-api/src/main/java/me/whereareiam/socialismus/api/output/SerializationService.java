package me.whereareiam.socialismus.api.output;

public interface SerializationService {
	<T> byte[] serialize(T object) throws Exception;

	<T> T deserialize(byte[] data, Class<T> clazz) throws Exception;
}