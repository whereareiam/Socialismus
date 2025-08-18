package me.whereareiam.socialismus.adapter.config.provider.base;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.adapter.config.deserializer.ChatTriggerDeserializer;
import me.whereareiam.socialismus.adapter.config.deserializer.ComponentDeserializer;
import me.whereareiam.socialismus.adapter.config.deserializer.RequirementDeserializer;
import me.whereareiam.socialismus.adapter.config.deserializer.VersionDeserializer;
import me.whereareiam.socialismus.adapter.config.serializer.ComponentSerializer;
import me.whereareiam.socialismus.api.model.chat.ChatTrigger;
import me.whereareiam.socialismus.api.model.requirement.Requirement;
import me.whereareiam.socialismus.api.type.Version;
import net.kyori.adventure.text.Component;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CoreJacksonModuleProvider implements Provider<Module> {
    private final ChatTriggerDeserializer chatTriggerDeserializer;
	private final RequirementDeserializer requirementDeserializer;
	private final VersionDeserializer versionDeserializer;
	private final ComponentDeserializer componentDeserializer;
	private final ComponentSerializer componentSerializer;

	@Override
	public Module get() {
		SimpleModule module = new SimpleModule();
		module.addDeserializer(ChatTrigger.class, chatTriggerDeserializer);
		module.addDeserializer(Requirement.class, requirementDeserializer);
		module.addDeserializer(Version.class, versionDeserializer);
		module.addDeserializer(Component.class, componentDeserializer);
		module.addSerializer(Component.class, componentSerializer);

		return module;
	}
}
