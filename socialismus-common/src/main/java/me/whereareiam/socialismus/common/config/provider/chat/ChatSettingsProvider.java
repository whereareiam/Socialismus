package me.whereareiam.socialismus.common.config.provider.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatSettingsDefaults;
import me.whereareiam.socialismus.common.config.provider.DefaultConfigProvider;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class ChatSettingsProvider extends DefaultConfigProvider<ChatSettings> {
	@Inject
	public ChatSettingsProvider(@Named("chatPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, "settings", ChatSettings.class, registry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(ChatSettingsDefaults.class), ChatSettings.class);
	}
}
