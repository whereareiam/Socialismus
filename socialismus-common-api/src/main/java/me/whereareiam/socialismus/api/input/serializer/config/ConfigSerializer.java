package me.whereareiam.socialismus.api.input.serializer.config;

import java.io.IOException;

public interface ConfigSerializer<T> {
	byte[] serialize(T value) throws IOException;
}