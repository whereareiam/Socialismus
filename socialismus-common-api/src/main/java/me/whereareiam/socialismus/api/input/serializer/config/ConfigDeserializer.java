package me.whereareiam.socialismus.api.input.serializer.config;

import java.io.IOException;

public interface ConfigDeserializer<T> {
	T deserialize(byte[] bytes) throws IOException;
}