package me.whereareiam.socialismus.common.config.provider.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatSyncDefaults;
import me.whereareiam.socialismus.common.config.provider.DefaultConfigProvider;
import me.whereareiam.socialismus.model.chat.ChatSync;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class ChatSyncProvider extends DefaultConfigProvider<ChatSync> {
	@Inject
	public ChatSyncProvider(@Named("chatPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, "sync", ChatSync.class, registry);
	}

	@Override
	protected Configura configura() {
		return super.configura().withDefaults(ChatSyncDefaults.class);
	}
}
