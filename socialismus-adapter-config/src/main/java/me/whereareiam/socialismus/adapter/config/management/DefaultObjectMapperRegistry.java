package me.whereareiam.socialismus.adapter.config.management;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.registry.ObjectMapperRegistry;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
public class DefaultObjectMapperRegistry implements ObjectMapperRegistry {
	private final List<Module> dynamicModules = new CopyOnWriteArrayList<>();
	private volatile ObjectMapper builtMapper;

	@Override
	@SuppressWarnings("unchecked")
	public <T> void addSerializer(Class<T> type, Object serializer) {
		SimpleModule module = new SimpleModule();
		module.addSerializer(type, (JsonSerializer<T>) serializer);

		dynamicModules.add(module);
		if (builtMapper != null)
			builtMapper.registerModule(module);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void addDeserializer(Class<T> type, Object deserializer) {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(type, (JsonDeserializer<T>) deserializer);

		dynamicModules.add(module);
		if (builtMapper != null)
			builtMapper.registerModule(module);
	}

	public void applyAllTo(ObjectMapper mapper) {
		this.builtMapper = mapper;
		for (Module mod : dynamicModules) {
			mapper.registerModule(mod);
		}
	}
}
