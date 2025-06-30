package me.whereareiam.socialismus.adapter.config.management;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.registry.ObjectMapperRegistry;
import me.whereareiam.socialismus.api.input.serializer.config.ConfigDeserializer;
import me.whereareiam.socialismus.api.input.serializer.config.ConfigSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class DefaultObjectMapperRegistry implements ObjectMapperRegistry {
	private final List<Module> dynamicModules = new CopyOnWriteArrayList<>();
	private volatile ObjectMapper builtMapper;

	@Override
	public <T> void addSerializer(Class<T> type, ConfigSerializer<? super T> serializer) {
		SimpleModule module = new SimpleModule();
		module.addSerializer(type, new JsonSerializer<T>() {
			@Override
			public void serialize(T value,
			                      JsonGenerator gen,
			                      SerializerProvider serializers) throws IOException {
				byte[] bytes = serializer.serialize(value);
				String rawJson = new String(bytes, StandardCharsets.UTF_8);
				gen.writeRawValue(rawJson);
			}
		});
		dynamicModules.add(module);
		if (builtMapper != null) {
			builtMapper.registerModule(module);
		}
	}

	@Override
	public <T> void addDeserializer(Class<T> type, ConfigDeserializer<? extends T> deserializer) {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(type, new JsonDeserializer<T>() {
			@Override
			public T deserialize(JsonParser p,
			                     DeserializationContext ctxt) throws IOException {
				JsonNode tree = p.readValueAsTree();
				String rawJson = tree.toString();
				return deserializer.deserialize(
						rawJson.getBytes(StandardCharsets.UTF_8)
				);
			}
		});
		dynamicModules.add(module);
		if (builtMapper != null) {
			builtMapper.registerModule(module);
		}
	}

	public void applyAllTo(ObjectMapper mapper) {
		this.builtMapper = mapper;
		for (Module mod : dynamicModules) {
			mapper.registerModule(mod);
		}
	}
}
