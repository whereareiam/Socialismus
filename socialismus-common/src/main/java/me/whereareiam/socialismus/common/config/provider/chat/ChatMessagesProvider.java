package me.whereareiam.socialismus.common.config.provider.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatMessagesDefaults;
import me.whereareiam.socialismus.common.config.provider.DefaultConfigProvider;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class ChatMessagesProvider extends DefaultConfigProvider<ChatMessages> {
	@Inject
	public ChatMessagesProvider(@Named("chatPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, "messages", ChatMessages.class, registry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(ChatMessagesDefaults.class), ChatMessages.class);
	}
}
