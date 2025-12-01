package me.whereareiam.socialismus.common.config.provider.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.input.registry.Registry;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.common.config.provider.DefaultConfigProvider;
import me.whereareiam.socialismus.common.config.template.chat.ChatSettingsTemplate;

import java.nio.file.Path;

@Singleton
public class ChatSettingsProvider extends DefaultConfigProvider<ChatSettings> {
	@Inject
	public ChatSettingsProvider(@Named("chatPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, registry);
	}

	@Override
	protected ChatSettings load() {
		return Config.update(getBasePath().resolve("settings"), ChatSettings.class);
	}

	@Override
	protected void registerTemplate() {
		Config.registerTemplate(ChatSettingsTemplate.class);
	}
}